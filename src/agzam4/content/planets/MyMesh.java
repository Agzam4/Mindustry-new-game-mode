package agzam4.content.planets;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.util.Log;
import arc.util.noise.Simplex;
import mindustry.graphics.Pal;
import mindustry.graphics.Shaders;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexMesher;
import mindustry.graphics.g3d.MeshBuilder;

public class MyMesh extends HexMesh {

	public MyMesh() {
		this.planet = NewGamePlanets.newSerpulo;
        this.shader = Shaders.planet;
        this.mesh = MeshBuilder.buildHex(new HexMesher(){
            @Override
            public float getHeight(Vec3 position){
            	Log.info(position);
                return position.x;//Simplex.noise3d(planet.id, 2, 0.55f, 0.45f, 5f + position.x, 5f + position.y, 5f + position.z);
            }

            @Override
            public Color getColor(Vec3 position){
                return Pal.spore;
            }
        }, 1, false, 1f, .1f);
	}
}
