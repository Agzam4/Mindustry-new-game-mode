package agzam4;

public class Vec1 {

	public float x;

    public Vec1(){
    }

    public Vec1(float x, float y){
        this.x = x;
    }

    public Vec1(Vec1 vec){
        this.x = vec.x;
    }
    
    public Vec1 set(float x){
        this.x = x;
        return this;
    }
    
    public Vec1 add(float x){
        this.x += x;
        return this;
    }

    @Override
    public String toString(){
        return "(" + x + ")";
    }
}
