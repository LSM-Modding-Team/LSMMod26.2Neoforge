"""Read the supplied Anvil world without modifying it; export school block evidence."""
import argparse, collections, gzip, json, struct, zipfile, zlib
from pathlib import Path

class Nbt:
    def __init__(self, data): self.data, self.i = data, 0
    def take(self, n):
        b = self.data[self.i:self.i+n]; self.i += n; return b
    def num(self, fmt): return struct.unpack('>'+fmt, self.take(struct.calcsize(fmt)))[0]
    def string(self): return self.take(self.num('H')).decode('utf-8', errors='replace')
    def tag(self, t):
        if t in range(1, 7): return self.num({1:'b',2:'h',3:'i',4:'q',5:'f',6:'d'}[t])
        if t == 7: return self.take(self.num('i'))
        if t == 8: return self.string()
        if t == 9:
            kind, n = self.num('B'), self.num('i'); return [self.tag(kind) for _ in range(n)]
        if t == 10:
            result = {}
            while (kind := self.num('B')):
                name = self.string(); result[name] = self.tag(kind)
            return result
        if t in (11,12): return [self.num('i' if t == 11 else 'q') for _ in range(self.num('i'))]
        raise ValueError(t)
    def root(self):
        t = self.num('B'); self.string(); return self.tag(t)

def read_world(path):
    blocks, entities = {}, []
    with zipfile.ZipFile(path) as z:
        for rx, rz in [(-1,-1),(-1,0)]:
            data = z.read(f'dimensions/minecraft/overworld/region/r.{rx}.{rz}.mca')
            for cx in range(-6,-3):
                for cz in range(-2,4):
                    if cx//32 != rx or cz//32 != rz: continue
                    idx = (cx%32 + (cz%32)*32)*4
                    offset = int.from_bytes(data[idx:idx+3], 'big')*4096
                    if not offset: continue
                    length = int.from_bytes(data[offset:offset+4], 'big')
                    kind = data[offset+4]; payload = data[offset+5:offset+4+length]
                    if kind not in (1,2,3): raise ValueError(f'Unsupported chunk compression: {kind}')
                    payload = zlib.decompress(payload) if kind == 2 else gzip.decompress(payload) if kind == 1 else payload
                    chunk = Nbt(payload).root()
                    entities.extend(chunk.get('block_entities', []))
                    for section in chunk.get('sections', []):
                        sy = section['Y']; states = section.get('block_states', {})
                        palette = states.get('palette', []); longs = states.get('data', [])
                        if not palette: continue
                        bits = max(4, (len(palette)-1).bit_length()); per = 64//bits
                        for k in range(4096):
                            x,y,zp = cx*16+k%16, sy*16+k//256, cz*16+(k//16)%16
                            if not (-87<=x<=-57 and 133<=y<=154 and -23<=zp<=56): continue
                            p = ((longs[k//per] & ((1<<64)-1)) >> ((k%per)*bits)) & ((1<<bits)-1) if longs else 0
                            blocks[x,y,zp] = palette[p]
    if len(blocks) != 31*22*80: raise ValueError('School bounds contain missing chunk sections')
    return blocks, entities

if __name__ == '__main__':
    ap = argparse.ArgumentParser(); ap.add_argument('world'); ap.add_argument('--output', default='build/school-analysis'); args=ap.parse_args()
    blocks, entities = read_world(args.world); out=Path(args.output); out.mkdir(parents=True,exist_ok=True)
    (out/'blocks.json').write_text(json.dumps([{'pos':p, **v} for p,v in blocks.items()]),encoding='utf-8')
    inside = [e for e in entities if -87<=e.get('x',0)<=-57 and -23<=e.get('z',0)<=56 and 133<=e.get('y',0)<=154]
    (out/'entities.json').write_text(json.dumps(inside,indent=2,ensure_ascii=False),encoding='utf-8')
    counts=collections.Counter(v['Name'] for v in blocks.values()); print(counts)
    for y in range(134,154):
        lines=[f'Y={y} x=-87..-57; z=-23..56']
        for z in range(-23,57):
            line=''
            for x in range(-87,-56):
                n=blocks.get((x,y,z),{}).get('Name','missing')
                ch=' ' if n.endswith(':air') or n.endswith(':cave_air') else 'D' if 'door' in n else 'g' if 'glass' in n else 's' if 'sign' in n else '.' if n.startswith('lsmmod:') else '#'
                line+=ch
            lines.append(f'{z:3} {line}')
        (out/f'layer-{y}.txt').write_text('\n'.join(lines))
    print('Block entities:',len(inside))
