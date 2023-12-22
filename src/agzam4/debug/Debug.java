package agzam4.debug;

import mindustry.Vars;

public class Debug {
	
//	public static void main(String[] args) throws Exception {
//		TestDrive testDrive = new TestDrive();
//
//		Lookup lookup = MethodHandles.lookup();
//
////        Field lookupImplField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
////        lookupImplField.setAccessible(true);
//        
//		Method method = testDrive.getClass().getDeclaredMethod("print");
//		System.out.println("ReturnType: " + method.getReturnType().getPackageName());
//		
//		boolean a = method.canAccess(testDrive);
//		
////		MethodType.methodType(null)
//		method.setAccessible(true);
//		
//        MethodHandle privateMethodHandle = lookup.findSpecial(
//        		TestDrive.class, method.getName(),
//                MethodType.methodType(method.getReturnType()),
//                TestDrive.class);
//		
//		
//		method.invoke(testDrive);
//		v1v
////        method.
//		
//		method.setAccessible(a);
//	}
	

	public static void init() {
//		Seq<T>
		Vars.mods.getScripts().context.evaluateString(Vars.mods.getScripts().scope,
				"var mod = Vars.mods.getMod(\"newgamemod\");\n"
				+ "var get = (pkg) => mod.loader.loadClass(pkg).newInstance();\n"
				+ "const NGUnitTypes = get(\"agzam4.content.units.NGUnitTypes\");\n"
				+ "const NewGameBlocks = get(\"agzam4.content.blocks.NewGameBlocks\");\n"
//				+ "const essence = get(\"agzam4.content.units.LifeUnitType.essence\");\n"
				+ "", "main.js", 0);
		
		
	}
	
	
}
