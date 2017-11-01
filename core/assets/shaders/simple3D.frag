
#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_diffuseTexture;
uniform sampler2D u_specularTexture;

uniform float u_usesDiffuseTexture;
uniform float u_usesSpecularTexture;

uniform vec4 globalAmbient;

uniform vec4 u_lightColorPos;
uniform vec4 u_lightColorDir;

uniform vec4 u_materialDiffuse;
uniform vec4 u_materialSpecular;
uniform float u_materialShininess;

varying vec2 v_uv;
varying vec4 v_normal;
varying vec4 v_sDir;
varying vec4 v_hDir;
varying vec4 v_sPos;
varying vec4 v_hPos;

void main()
{
    vec4 materialDiffuse;
    if (u_usesDiffuseTexture == 1.0) {
        materialDiffuse = texture2D(u_diffuseTexture, v_uv) * u_materialDiffuse;
    } else {
        materialDiffuse = u_materialDiffuse;
    }

    vec4 materialSpecular;
    if (u_usesSpecularTexture == 1.0) {
        materialSpecular = texture2D(u_specularTexture, v_uv) * u_materialDiffuse;
    } else {
        materialSpecular = u_materialSpecular;
    }

    // Directional light
    float lambert = max(0, dot(v_normal, v_sDir) / (length(v_normal) * length(v_sDir)));
    float phong = max(0, dot(v_normal, v_hDir) / (length(v_normal) * length(v_hDir)));

	vec4 diffuseColorDir = lambert * u_lightColorDir * materialDiffuse;
    vec4 specularColorDir = pow(phong, u_materialShininess) * u_lightColorDir * materialSpecular;
    vec4 lightDir = diffuseColorDir + specularColorDir;

    // Positional light
    lambert = max(0, dot(v_normal, v_sPos) / (length(v_normal) * length(v_sPos)));
    phong = max(0, dot(v_normal, v_hPos) / (length(v_normal) * length(v_hPos)));

	vec4 diffuseColorPos = lambert * u_lightColorPos * materialDiffuse;
    vec4 specularColorPos = pow(phong, u_materialShininess) * u_lightColorPos * u_materialSpecular;
    vec4 lightPos = diffuseColorPos + specularColorPos;

	gl_FragColor = globalAmbient * materialDiffuse + lightDir + lightPos;
	gl_FragColor.a = u_materialDiffuse.a;
}