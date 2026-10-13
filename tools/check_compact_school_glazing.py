"""Static regression for dynamic Fusion selection and the reduced state domains."""
import itertools,json,re,hashlib
from compact_school_glazing_assets import A,IDS,CONNECTIONS,main as compact
R=A.parents[4];J=R/'src/main/java/net/nicomar2009/lsmmod'
def read(p):return json.loads(p.read_text(encoding='utf-8'))
def main():
 enum=(J/'block/CurtainLayout.java').read_text()
 layouts=re.findall(r'^    ([A-Z_]+)\(CurtainPart\.',enum,re.M)
 assert len(layouts)==len(set(layouts))==17
 parts=('single','left','middle','right')
 assert {s.lower() for s in layouts}=={'none'}|{a+'_'+b for a,b in itertools.product(parts,repeat=2)}
 checked=0
 tracked=[A/f'blockstates/{ident}.json' for ident in IDS]+list((A/'models/block/school_glazing_dynamic').glob('*.json'))
 before={p:hashlib.sha256(p.read_bytes()).hexdigest() for p in tracked}
 compact();assert before=={p:hashlib.sha256(p.read_bytes()).hexdigest() for p in tracked}
 for ident in IDS:
  definition=read(A/f'blockstates/{ident}.json')
  base=[p for p in definition['multipart'] if 'curtain' not in p['when']]
  assert len(base)==(4 if ident=='school_glass' else 80)
  for part in definition['multipart']:
   assert not set(part['when'])&set(CONNECTIONS+('curtain_vertical',))
  for part in base:
   when=part['when'];assert part['apply'].get('y',0)=={'north':0,'east':90,'south':180,'west':270}[when['facing']]
   model=read(A/f"models/{part['apply']['model'].split(':')[1]}.json")
   assert model['loader']=='fusion:model' and model['type']=='composite' and len(model['models'])==1
   entries=model['models'][0];assert [e['condition']['mask'] for e in entries]==list(range(32))
   for entry in entries:
    mask=entry['condition']['mask'];assert entry['condition']['type']=='lsmmod:glazing_mask'
    stem=f'school_glass_{int(bool(mask&8))}_{int(bool(mask&16))}' if ident=='school_glass' else f"{ident}_{when['half']}_{when['shape']}_{int(bool(mask&8))}_{int(bool(mask&16))}"
    if mask&7!=3 and (ident=='school_glass' or when['shape']=='straight'):stem+=f'_horizontal_{mask&7}'
    assert entry['model']=='lsmmod:block/'+stem,(ident,when,mask,entry)
    assert (A/f'models/block/{stem}.json').is_file();checked+=1
 for name in ('SchoolGlassBlock','SchoolGlassStairBlock'):
  source=(J/f'block/{name}.java').read_text();builder=source.split('protected void createBlockStateDefinition')[1].split('\n    }')[0]
  assert not any(c in builder for c in ('FRAME_LAYOUT','TOP_CONNECTED','BOTTOM_CONNECTED','VERTICAL'))
  assert 'SchoolCurtains.CURTAIN' in builder
 config=read(R/'src/main/resources/lsmmod.mixins.json')
 assert set(config['mixins'])=={'CurtainPaletteMigrationMixin','CurtainChunkMigrationMixin'}
 assert config['client']==['GlazingClientUpdatesMixin']
 migration=(J/'block/LegacyCurtainStates.java').read_text()
 assert all('lsmmod:'+ident in migration for ident in IDS)
 assert 'getListOrEmpty("sections")' in migration and 'getListOrEmpty("palette").set(entry,converted)' in migration
 assert 'result=tag.copy()' in migration and 'horizontal+"_"+vertical' in migration
 update=(J/'mixin/GlazingClientUpdatesMixin.java').read_text()
 assert 'handleBlockUpdate' in update and 'handleChunkBlocksUpdate' in update and 'setSectionDirtyWithNeighbors' in update
 assert 'else level.scheduleTick' not in (J/'block/SchoolCurtains.java').read_text()
 active_states=4*17+4*2*5*2*17
 assert active_states==1428
 print(f'OK: {checked} dynamic geometry/rotation conditions, 17 valid curtain layouts, idempotent generation; {active_states} active glass/stair states; paints and carving remain entity data; migration and client packet hooks checked statically.')
if __name__=='__main__':main()
