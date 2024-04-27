$input v_texcoord0, v_color0, v_light, v_fog, v_glintUV

#include <bgfx_shader.sh>
#include <MinecraftRenderer.Materials/DynamicUtil.dragonh>
#include <MinecraftRenderer.Materials/FogUtil.dragonh>
#include <MinecraftRenderer.Materials/GlintUtil.dragonh>

uniform vec4 ChangeColor;
uniform vec4 OverlayColor;
uniform vec4 ColorBased;
uniform vec4 MultiplicativeTintColor;
uniform vec4 TileLightColor;
uniform vec4 GlintColor;

SAMPLER2D(s_GlintTexture, 0);

void main() {
    #if DEPTH_ONLY
        gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
        return;
    #endif

    vec4 albedo;
    albedo.rgb = mix(vec3(1.0, 1.0, 1.0), v_color0.rgb, ColorBased.x);
    albedo.a = 1.0;

    #if MULTI_COLOR_TINT
        albedo = applyMultiColorChange(albedo, ChangeColor.rgb, MultiplicativeTintColor.rgb);
    #else
        albedo = applyColorChange(albedo, ChangeColor, v_color0.a);
    #endif

    albedo = applyOverlayColor(albedo, OverlayColor);
    albedo = applyLighting(albedo, v_light);
    albedo = applyGlint(albedo, v_glintUV, s_GlintTexture, GlintColor, v_light);

    #if ALPHA_TEST
        if (albedo.a < 0.5) {
            discard;
        }
    #endif

    albedo.rgb = applyFog(albedo.rgb, v_fog.rgb, v_fog.a);

    gl_FragColor = albedo;
}
