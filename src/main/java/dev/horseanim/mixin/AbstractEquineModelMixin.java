package dev.horseanim.mixin;

import dev.horseanim.HorseAnimator;
import net.minecraft.client.model.animal.equine.AbstractEquineModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cavalo, burro, mula, cavalo-esqueleto, cavalo-zumbi e as camadas de sela e armadura
 * usam todos este modelo base, então um único ponto de entrada cobre todos eles.
 */
@Mixin(AbstractEquineModel.class)
public abstract class AbstractEquineModelMixin {
    @Shadow @Final protected ModelPart body;
    @Shadow @Final protected ModelPart headParts;
    @Shadow @Final private ModelPart rightHindLeg;
    @Shadow @Final private ModelPart leftHindLeg;
    @Shadow @Final private ModelPart rightFrontLeg;
    @Shadow @Final private ModelPart leftFrontLeg;
    @Shadow @Final private ModelPart tail;

    @Inject(
        method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/EquineRenderState;)V",
        at = @At("TAIL")
    )
    private void horseanim$improveAnimation(EquineRenderState state, CallbackInfo ci) {
        HorseAnimator.apply(state, this.body, this.headParts,
                this.leftHindLeg, this.rightHindLeg,
                this.leftFrontLeg, this.rightFrontLeg, this.tail);
    }
}
