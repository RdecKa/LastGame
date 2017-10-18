
#ifdef GL_ES
precision mediump float;
#endif

attribute vec3 a_position;
attribute vec3 a_normal;

uniform mat4 u_modelMatrix;
uniform mat4 u_viewMatrix;
uniform mat4 u_projectionMatrix;

uniform vec4 u_eyePosition;

uniform vec4 u_lightPositionDir; // In global coordinates; directional light
uniform vec4 u_lightPositionPos; // positional light

varying vec4 v_normal;
varying vec4 v_sDir;
varying vec4 v_hDir;
varying vec4 v_sPos;
varying vec4 v_hPos;

void main()
{
	vec4 position = vec4(a_position.x, a_position.y, a_position.z, 1.0);
	position = u_modelMatrix * position;

	vec4 normal = vec4(a_normal.x, a_normal.y, a_normal.z, 0.0);
	normal = u_modelMatrix * normal;

	// Global coordinates
	//  Ligthing

	v_normal = normal;
	vec4 v = u_eyePosition - position; // Direction to the camera


	// Directional light
    if (u_lightPositionDir[3] == 1.0) {
    	v_sDir = u_lightPositionDir - position; // Direction to the light
    } else {
        v_sDir = vec4(u_lightPositionDir.x, u_lightPositionDir.y, u_lightPositionDir.z, 0);
    }
	v_hDir = v_sDir + v;

    // Positional light
	if (u_lightPositionPos[3] == 1.0) {
    	v_sPos = u_lightPositionPos - position; // Direction to the light
    } else {
        v_sPos = vec4(u_lightPositionPos.x, u_lightPositionPos.y, u_lightPositionPos.z, 0);
    }
	v_hPos = v_sPos + v;



	position = u_viewMatrix * position;

	gl_Position = u_projectionMatrix * position;
}