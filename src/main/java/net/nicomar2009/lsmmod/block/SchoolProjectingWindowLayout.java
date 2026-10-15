package net.nicomar2009.lsmmod.block;

import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

/** Uniform panes for explicitly listed, complete rectangular glass openings. Saved block properties stay intact. */
public final class SchoolProjectingWindowLayout {
    private SchoolProjectingWindowLayout() {}
    public record Rule(int width,int height,int columns,int rows) {
        public String id() {return "w"+width+"h"+height;}
        public int cells(int start) {return (start+width+15)/16;}
        public int rows(int startY) {return (startY+height+15)/16;}
        public List<Part> parts(int start,int x,int y) {return parts(start,0,x,y);}
        public List<Part> parts(int start,int startY,int x,int y) {
            List<Part> result=new ArrayList<>();
            double pw=(double)width/columns,ph=(double)height/rows;
            for(int row=0;row<rows;row++)for(int column=0;column<columns;column++) {
                double ox=start+column*pw-x*16,oy=startY+row*ph-y*16;
                double x0=Math.max(Math.max(0,start-x*16),ox),x1=Math.min(Math.min(16,start+width-x*16),ox+pw);
                double y0=Math.max(0,oy),y1=Math.min(16,oy+ph);
                if(x0<x1-1e-8&&y0<y1-1e-8)result.add(new Part(x0,x1,y0,y1,ox,oy,pw,ph,column,row));
            }
            return List.copyOf(result);
        }
    }
    public record Part(double x0,double x1,double y0,double y1,double ox,double oy,double width,double height,int column,int row) {}
    public record View(Rule rule,BlockPos origin,int start,int startY,int x,int y) {
        public String key() {return rule.id()+"_"+start+"_"+x+"_"+y+(startY==0?"":"_v"+startY);}
        public List<Part> parts() {return rule.parts(start,startY,x,y);}
        public List<Part> parts(BlockState state) {
            int low=SchoolProjectingWindowBlock.glassLowY(state),high=SchoolProjectingWindowBlock.glassHighY(state);
            return parts().stream().map(p->new Part(p.x0,p.x1,Math.max(low,p.y0),Math.min(high,p.y1),p.ox,p.oy,p.width,p.height,p.column,p.row))
                    .filter(p->p.y0<p.y1).toList();
        }
        public BlockPos controller(Direction right,Part part) {
            return origin.relative(right,(int)Math.floor((start+part.column*(double)rule.width/rule.columns)/16))
                    .above((int)Math.floor((startY+part.row*(double)rule.height/rule.rows)/16));
        }
        public int pose(BlockGetter level,Direction facing,Part part) {
            BlockState state=read(level,controller(facing.getClockWise(),part));
            return window(state,facing)&&state.getValue(SchoolProjectingWindowBlock.OPEN)?1:0;
        }
    }
    public static final List<Rule> RULES=loadRules("projecting_window_layouts.json",false);
    // Only select layouts whose models were generated. A table-only resource override
    // must retain the legacy sash instead of suppressing every model.
    public static final List<Rule> BAKED_RULES=loadRules("projecting_window_baked_layouts.json",true);
    private static List<Rule> loadRules(String name,boolean optional) {
        try(var stream=SchoolProjectingWindowLayout.class.getResourceAsStream("/assets/lsmmod/"+name)) {
            if(stream==null&&optional)return List.of();
            if(stream==null)throw new IllegalStateException("Missing projecting window layouts");
            List<Rule> rules=new ArrayList<>();
            for(var element:JsonParser.parseReader(new InputStreamReader(stream,StandardCharsets.UTF_8)).getAsJsonArray()) {
                var entry=element.getAsJsonObject();double w=entry.get("width").getAsDouble()*16,h=entry.get("height").getAsDouble()*16;
                int columns=entry.get("columns").getAsInt(),rows=entry.get("rows").getAsInt();
                if(w!=Math.rint(w)||h!=Math.rint(h)||((int)w)%8!=0||((int)h)%8!=0
                        ||columns<1||rows<1||w/columns<16||h/rows<16||w>1024||h>1024)
                    throw new IllegalStateException("Unsupported projecting window layout: "+entry);
                Rule rule=new Rule((int)w,(int)h,columns,rows);
                if(rules.stream().anyMatch(old->old.width==rule.width&&old.height==rule.height))throw new IllegalStateException("Duplicate projecting window layout");
                rules.add(rule);
            }
            return List.copyOf(rules);
        } catch(java.io.IOException error) {throw new IllegalStateException("Cannot read projecting window layouts",error);}
    }
    private static BlockState read(BlockGetter level,BlockPos pos) {
        if(level==null||(level instanceof LevelReader reader&&!reader.hasChunkAt(pos)))return null;
        return level.getBlockState(pos);
    }
    private static boolean window(BlockState state,Direction facing) {
        return state!=null&&state.getBlock() instanceof SchoolProjectingWindowBlock
                &&SchoolProjectingWindowBlock.glassLowY(state)<SchoolProjectingWindowBlock.glassHighY(state)
                &&state.hasProperty(SchoolProjectingWindowBlock.FACING)&&state.getValue(SchoolProjectingWindowBlock.FACING)==facing;
    }
    private static int low(BlockState state) {return SchoolProjectingWindowBlock.halfWindow(state)&&state.getValue(SchoolProjectingWindowBlock.WALL_LEFT)?8:0;}
    private static int high(BlockState state) {return SchoolProjectingWindowBlock.halfWindow(state)&&!state.getValue(SchoolProjectingWindowBlock.WALL_LEFT)?8:16;}
    private static boolean connected(BlockState a,BlockState b,Direction direction,Direction facing) {
        if(!window(a,facing)||!window(b,facing))return false;
        if(direction.getAxis()==Direction.Axis.Y)return Math.max(low(a),low(b))<Math.min(high(a),high(b))
                &&(direction==Direction.UP?SchoolProjectingWindowBlock.glassHighY(a)==16&&SchoolProjectingWindowBlock.glassLowY(b)==0
                :SchoolProjectingWindowBlock.glassLowY(a)==0&&SchoolProjectingWindowBlock.glassHighY(b)==16);
        if(Math.max(SchoolProjectingWindowBlock.glassLowY(a),SchoolProjectingWindowBlock.glassLowY(b))
                >=Math.min(SchoolProjectingWindowBlock.glassHighY(a),SchoolProjectingWindowBlock.glassHighY(b)))return false;
        return direction==facing.getClockWise()?high(a)==16&&low(b)==0:low(a)==0&&high(b)==16;
    }
    private static int extent(BlockGetter level,BlockPos pos,Direction direction,Direction facing,int limit) {
        BlockState previous=read(level,pos);
        for(int step=1;step<=limit;step++) {
            BlockState next=read(level,pos.relative(direction,step));
            if(!connected(previous,next,direction,facing))return step-1;
            previous=next;
        }
        return -1; // Beyond configured bounds: no truncated match.
    }
    /** Use the supplied world/snapshot only; never load chunks or borrow the live client world. */
    public static View find(BlockGetter level,BlockPos pos,BlockState state) {
        if(pos==null||state==null||!(state.getBlock() instanceof SchoolProjectingWindowBlock)
                ||!state.hasProperty(SchoolProjectingWindowBlock.FACING))return null;
        Direction facing=state.getValue(SchoolProjectingWindowBlock.FACING),right=facing.getClockWise();
        BlockState current=read(level,pos);
        if(!window(current,facing)||current.getBlock()!=state.getBlock()
                ||SchoolProjectingWindowBlock.glassLowY(current)!=SchoolProjectingWindowBlock.glassLowY(state)
                ||SchoolProjectingWindowBlock.glassHighY(current)!=SchoolProjectingWindowBlock.glassHighY(state)
                ||(SchoolProjectingWindowBlock.halfWindow(state)&&current.getValue(SchoolProjectingWindowBlock.WALL_LEFT)!=state.getValue(SchoolProjectingWindowBlock.WALL_LEFT)))return null;
        int maxX=RULES.stream().mapToInt(rule->rule.cells(8)).max().orElse(0),maxY=RULES.stream().mapToInt(rule->rule.rows(8)).max().orElse(0);
        int left=extent(level,pos,right.getOpposite(),facing,maxX),east=extent(level,pos,right,facing,maxX);
        int down=extent(level,pos,Direction.DOWN,facing,maxY),up=extent(level,pos,Direction.UP,facing,maxY);
        if(left<0||east<0||down<0||up<0)return null;
        int cells=left+east+1,height=down+up+1;BlockPos origin=pos.relative(right,-left).below(down);
        BlockState first=read(level,origin),last=read(level,origin.relative(right,cells-1));
        if(!window(first,facing)||!window(last,facing))return null;
        BlockState top=read(level,origin.above(height-1));if(!window(top,facing))return null;
        int start=low(first),end=(cells-1)*16+high(last),width=end-start;
        int startY=SchoolProjectingWindowBlock.glassLowY(first),endY=(height-1)*16+SchoolProjectingWindowBlock.glassHighY(top);
        Rule rule=RULES.stream().filter(BAKED_RULES::contains).filter(candidate->candidate.width==width&&candidate.height==endY-startY).findFirst().orElse(null);
        if(rule==null)return null;
        for(int y=0;y<height;y++)for(int x=0;x<cells;x++) {
            BlockPos cell=origin.relative(right,x).above(y);BlockState value=read(level,cell);
            if(!window(value,facing)||low(value)!=Math.max(0,start-x*16)||high(value)!=Math.min(16,end-x*16)
                    ||SchoolProjectingWindowBlock.glassLowY(value)!=Math.max(0,startY-y*16)
                    ||SchoolProjectingWindowBlock.glassHighY(value)!=Math.min(16,endY-y*16))return null;
            // Reject ragged extensions and partial snapshots rather than selecting a rectangle within them.
            for(Direction edge:new Direction[]{Direction.DOWN,Direction.UP,right.getOpposite(),right}) {
                if((edge==Direction.DOWN&&y==0)||(edge==Direction.UP&&y==height-1)
                        ||(edge==right.getOpposite()&&x==0)||(edge==right&&x==cells-1)) {
                    BlockState neighbor=read(level,cell.relative(edge));
                    if(neighbor==null||connected(value,neighbor,edge,facing))return null;
                }
            }
        }
        return new View(rule,origin,start,startY,left,down);
    }
    public static boolean validKey(String key) {
        for(Rule rule:BAKED_RULES)for(int start:new int[]{0,8})for(int sy:new int[]{0,8})
            for(int y=0;y<rule.rows(sy);y++)for(int x=0;x<rule.cells(start);x++)
                if(key.equals(rule.id()+"_"+start+"_"+x+"_"+y+(sy==0?"":"_v"+sy)))return true;
        return false;
    }
}
