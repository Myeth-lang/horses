package dev.horseanim;

/**
 * Matemática da marcha dos cavalos. Não depende de nada do Minecraft.
 *
 * Convenções (iguais às do modelo do jogo):
 *  - ângulo X da pata: positivo = pata para trás, negativo = para frente
 *  - subida do corpo, em pixels do modelo: positivo = para cima
 *  - pitch do corpo, em radianos: positivo = focinho para cima
 *  - a fase 0 é o instante em que a pata dianteira esquerda toca o chão. É também o ponto
 *    mais baixo do balanço de cabeça do jogo base, então cabeça e pernas ficam sincronizadas.
 */
public final class HorseGait {
    public static final int HIND_L = 0;
    public static final int HIND_R = 1;
    public static final int FRONT_L = 2;
    public static final int FRONT_R = 3;

    private static final float TAU = (float) (Math.PI * 2.0);

    // Instante em que cada pata toca o chão, em fração do ciclo.
    // Ordem: traseira esq., traseira dir., dianteira esq., dianteira dir.
    private static final float[] STEP_WALK   = {0.75F,  0.25F,  0.00F, 0.50F}; // 4 tempos
    private static final float[] STEP_TROT   = {0.50F,  0.00F,  0.00F, 0.50F}; // diagonais juntas
    private static final float[] STEP_GALLOP = {0.70F, -0.20F, -0.05F, 0.05F}; // traseiras, depois dianteiras

    // Amplitude do balanço (radianos)
    private static final float[] AMP_WALK   = {0.34F, 0.34F, 0.40F, 0.40F};
    private static final float[] AMP_TROT   = {0.52F, 0.52F, 0.62F, 0.62F};
    private static final float[] AMP_GALLOP = {0.72F, 0.72F, 0.85F, 0.85F};

    // No galope as dianteiras ficam mais esticadas e as traseiras mais empurrando
    private static final float[] BIAS_GALLOP = {0.10F, 0.10F, -0.10F, -0.10F};

    private HorseGait() {}

    // ---- utilidades -------------------------------------------------------------------------

    public static float lerp(float t, float a, float b) {
        return a + (b - a) * t;
    }

    public static float clamp01(float v) {
        return Math.min(1.0F, Math.max(0.0F, v));
    }

    public static float smooth(float edge0, float edge1, float x) {
        float t = clamp01((x - edge0) / (edge1 - edge0));
        return t * t * (3.0F - 2.0F * t);
    }

    /** 0 = andando, 1 = trote */
    private static float toTrot(float speed) {
        return smooth(0.30F, 0.60F, speed);
    }

    /** 0 = trote, 1 = galope */
    private static float toGallop(float speed) {
        return smooth(0.80F, 0.97F, speed);
    }

    /** 0 = parado, 1 = em movimento */
    private static float moving(float speed) {
        return smooth(0.0F, 0.30F, speed);
    }

    private static float step(int leg, float speed) {
        float t = toTrot(speed);
        float g = toGallop(speed);
        return lerp(g, lerp(t, STEP_WALK[leg], STEP_TROT[leg]), STEP_GALLOP[leg]);
    }

    // ---- pernas -----------------------------------------------------------------------------

    /** Ângulo X da pata em radianos. */
    public static float swing(int leg, float phase, float speed) {
        float t = toTrot(speed);
        float g = toGallop(speed);
        float amp = lerp(g, lerp(t, AMP_WALK[leg], AMP_TROT[leg]), AMP_GALLOP[leg]);
        float angle = BIAS_GALLOP[leg] * g - amp * (float) Math.cos(phase - TAU * step(leg, speed));
        return angle * moving(speed);
    }

    /** 0..1: quanto a pata está erguida (fase em que ela volta para a frente). */
    public static float lift(int leg, float phase, float speed) {
        float s = -(float) Math.sin(phase - TAU * step(leg, speed));
        return Math.max(0.0F, s) * moving(speed);
    }

    // ---- corpo ------------------------------------------------------------------------------

    /** Subida do corpo em pixels. No galope o corpo sobe na fase de suspensão. */
    public static float rise(float phase, float speed) {
        float t = toTrot(speed);
        float g = toGallop(speed);
        float walk = 0.20F * (float) Math.cos(2.0F * phase - 1.5F);
        float trot = 0.50F * (float) Math.cos(2.0F * phase - 5.34F);
        float gallop = -0.70F * (float) Math.cos(phase);
        return lerp(g, lerp(t, walk, trot), gallop) * moving(speed);
    }

    /** Inclinação do corpo em radianos (focinho para cima = positivo). */
    public static float pitch(float phase, float speed) {
        float t = toTrot(speed);
        float g = toGallop(speed);
        float walk = 0.020F * (float) Math.sin(phase);
        float trot = 0.012F * (float) Math.cos(2.0F * phase - 5.34F);
        float gallop = -0.050F * (float) Math.sin(phase + 0.5F);
        return lerp(g, lerp(t, walk, trot), gallop) * moving(speed);
    }

    // ---- rabo -------------------------------------------------------------------------------

    /** Balanço lateral do rabo, defasado em relação à passada. */
    public static float tailSway(float phase, float speed) {
        return 0.20F * (float) Math.sin(phase - 1.3F) * moving(speed);
    }

    /** Sobe e desce do rabo. */
    public static float tailFlick(float phase, float speed) {
        return 0.08F * (float) Math.sin(phase - 0.8F) * moving(speed);
    }
}
