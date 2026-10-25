package dev.horseanim;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.EquineRenderState;

/**
 * Aplica a animação nova por cima do que o jogo base já calculou no quadro atual.
 * O modelo é reiniciado a cada quadro pelo jogo, então os ajustes não acumulam.
 */
public final class HorseAnimator {
    /** Quanto a pata sobe do chão quando dá o passo, em pixels do modelo. */
    private static final float FOOT_LIFT_PX = 1.2F;
    /** Distância (px) do centro do corpo até o pescoço e até as patas dianteiras. */
    private static final float NECK_ARM_PX = 17.0F;
    private static final float FRONT_LEG_ARM_PX = 14.0F;

    private HorseAnimator() {}

    public static void apply(EquineRenderState s, ModelPart body, ModelPart head,
                             ModelPart hindL, ModelPart hindR,
                             ModelPart frontL, ModelPart frontR, ModelPart tail) {
        HorseAnimConfig c = HorseAnimConfig.get();
        if (!c.enabled) {
            return;
        }

        float speed = HorseGait.clamp01(s.walkAnimationSpeed);
        float rearing = HorseGait.clamp01(s.standAnimation);  // empinando
        float grazing = HorseGait.clamp01(s.eatAnimation);    // pastando
        float calm = (1.0F - rearing) * (1.0F - grazing);     // peso dos movimentos normais
        float phase = s.walkAnimationPos * 0.8F;              // mesma frequência do balanço de cabeça do jogo
        float age = s.ageInTicks;

        // 1) Pernas: troca o trote único do jogo por andar / trote / galope
        float mix = HorseGait.clamp01(c.gaits) * (1.0F - rearing);
        if (mix > 0.0F) {
            hindL.xRot = HorseGait.lerp(mix, hindL.xRot, HorseGait.swing(HorseGait.HIND_L, phase, speed) * c.legSwing);
            hindR.xRot = HorseGait.lerp(mix, hindR.xRot, HorseGait.swing(HorseGait.HIND_R, phase, speed) * c.legSwing);
            frontL.xRot = HorseGait.lerp(mix, frontL.xRot, HorseGait.swing(HorseGait.FRONT_L, phase, speed) * c.legSwing);
            frontR.xRot = HorseGait.lerp(mix, frontR.xRot, HorseGait.swing(HorseGait.FRONT_R, phase, speed) * c.legSwing);

            float lift = FOOT_LIFT_PX * c.footLift * mix;
            hindL.y -= HorseGait.lift(HorseGait.HIND_L, phase, speed) * lift;
            hindR.y -= HorseGait.lift(HorseGait.HIND_R, phase, speed) * lift;
            frontL.y -= HorseGait.lift(HorseGait.FRONT_L, phase, speed) * lift;
            frontR.y -= HorseGait.lift(HorseGait.FRONT_R, phase, speed) * lift;
        }

        // 2) Corpo: sobe e desce com a passada e respira quando parado
        float rise = HorseGait.rise(phase, speed) * c.bodyBob * (s.isRidden ? 0.5F : 1.0F);
        rise += (float) Math.sin(age * 0.09F) * 0.20F * (1.0F - speed) * c.idle;
        rise *= calm;

        float pitch = HorseGait.pitch(phase, speed) * c.bodyPitch * calm;

        // cabeça e pernas são peças separadas do corpo: sobem junto para não abrir fresta
        body.y -= rise;
        head.y -= rise + pitch * NECK_ARM_PX;
        hindL.y -= rise;
        hindR.y -= rise;
        frontL.y -= rise + pitch * FRONT_LEG_ARM_PX;
        frontR.y -= rise + pitch * FRONT_LEG_ARM_PX;
        body.xRot -= pitch;

        // 3) Cabeça: o balanço do jogo base já acompanha as pernas; aqui só ajusta a intensidade
        if (c.headNod != 1.0F && speed > 0.2F) {
            float vanillaNod = (float) Math.cos(s.walkAnimationPos * 0.8F) * 0.15F * speed;
            head.xRot += (c.headNod - 1.0F) * vanillaNod * calm;
        }

        // 4) Rabo: balança com atraso em relação à passada e fica solto quando parado
        float sway = HorseGait.tailSway(phase, speed) * c.tailSway;
        sway += (float) Math.sin(age * 0.07F) * 0.06F * (1.0F - speed) * c.idle;
        tail.yRot += sway * (1.0F - rearing);
        tail.xRot += HorseGait.tailFlick(phase, speed) * c.tailSway * (1.0F - rearing);
    }
}
