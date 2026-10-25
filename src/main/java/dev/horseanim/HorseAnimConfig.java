package dev.horseanim;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Intensidades ajustáveis. Arquivo: config/better-horse-animations.json
 * 1.0 = padrão do mod, 0.0 = desliga aquele efeito, 2.0 = o dobro.
 */
public final class HorseAnimConfig {
    public boolean enabled = true;
    public float gaits = 1.0F;      // marcha nova: andar, trote e galope (0 = pernas do jogo base)
    public float legSwing = 1.0F;   // amplitude do balanço das pernas
    public float footLift = 1.0F;   // quanto a pata sobe do chão ao dar o passo
    public float bodyBob = 1.0F;    // sobe e desce do corpo
    public float bodyPitch = 1.0F;  // balanço do corpo para frente e para trás
    public float headNod = 1.0F;    // cabeçada da corrida (1 = como no jogo base)
    public float tailSway = 1.0F;   // rabo balançando com a passada
    public float idle = 1.0F;       // respiração e rabo parado

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static HorseAnimConfig instance;

    public static HorseAnimConfig get() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    private static HorseAnimConfig load() {
        Path file = FabricLoader.getInstance().getConfigDir().resolve("better-horse-animations.json");
        HorseAnimConfig cfg = null;

        if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file)) {
                cfg = GSON.fromJson(reader, HorseAnimConfig.class);
            } catch (Exception ignored) {
                // arquivo com erro: volta para os valores padrão
            }
        }
        if (cfg == null) {
            cfg = new HorseAnimConfig();
        }

        // regrava para o arquivo sempre listar todas as opções
        try (Writer writer = Files.newBufferedWriter(file)) {
            GSON.toJson(cfg, writer);
        } catch (IOException ignored) {
            // sem permissão de escrita: segue com os valores em memória
        }
        return cfg;
    }
}
