#version 300 es
precision mediump float;

uniform sampler2D u_CameraDepthTexture;
uniform sampler2D u_ColorMap;

in vec2 v_CameraTexCoord;

layout(location = 0) out vec4 o_FragColor;

float Depth_GetCameraDepthInMillimeters(const sampler2D depthTexture,
                                        const vec2 depthUv) {

  vec3 packedDepthAndVisibility = texture(depthTexture, depthUv).xyz;
  return dot(packedDepthAndVisibility.xy, vec2(255.0, 256.0 * 255.0));
}

vec3 Depth_GetColorVisualization(float x) {
  return texture(u_ColorMap, vec2(x, 0.5)).rgb;
}

float InverseLerp(float value, float min_bound, float max_bound) {
  return clamp((value - min_bound) / (max_bound - min_bound), 0.0, 1.0);
}

void main() {
  const float kMidDepthMeters = 8.0;
  const float kMaxDepthMeters = 30.0;

  float depth_mm =
      Depth_GetCameraDepthInMillimeters(u_CameraDepthTexture, v_CameraTexCoord);
  float depth_meters = depth_mm * 0.001;

  float normalizedDepth = 0.0;
  if (depth_meters < kMidDepthMeters) {
    normalizedDepth = InverseLerp(depth_meters, 0.0, kMidDepthMeters) * 0.5;
  } else {

    normalizedDepth =
        InverseLerp(depth_meters, kMidDepthMeters, kMaxDepthMeters) * 0.5 + 0.5;
  }

  vec4 depth_color = vec4(Depth_GetColorVisualization(normalizedDepth), 1.0);

  // Invalid depth (pixels with value 0) mapped to black.
  depth_color.rgb *= sign(depth_meters);
  o_FragColor = depth_color;
}
