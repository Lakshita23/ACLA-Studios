package com.aclastudios.spaceconquest.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.aclastudios.spaceconquest.SpaceConquest;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//		config.useGL30 = true;
//		ShaderProgram.prependVertexCode = "#version 140\n#define varying out\n#define attribute in\n";
//		ShaderProgram.prependFragmentCode = "#version 140\n#define varying in\n#define texture2D texture\n#define gl_FragColor fragColor\nout vec4 fragColor;\n";
		new LwjglApplication(new SpaceConquest(), config);
	}
}
