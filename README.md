# Better Horse Animations (Fabric, Minecraft Java 26.3)

Mod **só de cliente** que melhora a animação de cavalos, burros, mulas, cavalos-esqueleto e cavalos-zumbi (incluindo sela e armadura):

- **Marcha de verdade**: anda em 4 tempos, trota com as diagonais juntas e galopa com as traseiras e dianteiras em pares (no jogo base é sempre o mesmo trote).
- **Pata que sai do chão** a cada passo, em vez de arrastar.
- **Corpo** sobe e desce e balança com a passada (suspensão no galope).
- **Rabo** balança com atraso em relação à passada e fica solto quando parado.
- **Respiração** leve quando o cavalo está parado.
- Empinar e pastar continuam como no jogo base.

## Requisitos para jogar
- Minecraft Java 26.3 com Fabric Loader 0.19.3 ou mais novo
- Java 25
- Fabric API **não** é necessário

## Como gerar o .jar

### Opção A: sem instalar nada (GitHub)
1. Crie um repositório no GitHub e suba o conteúdo desta pasta (inclusive a pasta `.github`).
2. Abra a aba **Actions**, escolha **build** e toque em **Run workflow**.
3. Quando terminar, baixe o artefato **better-horse-animations** (é um zip com o `.jar`).

### Opção B: no PC
1. Instale o JDK 25 e o Gradle 9.7.1.
2. Na pasta do projeto: `gradle build`
3. O arquivo fica em `build/libs/better-horse-animations-1.0.0.jar`

Copie o `.jar` para a pasta `mods` do perfil Fabric 26.3.

## Ajustes
Na primeira execução o mod cria `config/better-horse-animations.json`.
Todos os valores são multiplicadores: `1.0` é o padrão, `0.0` desliga o efeito, `2.0` dobra.

| Opção | O que faz |
|---|---|
| `enabled` | liga/desliga o mod |
| `gaits` | marcha nova (0 = pernas do jogo base) |
| `legSwing` | amplitude das pernas |
| `footLift` | altura do passo |
| `bodyBob` | sobe e desce do corpo |
| `bodyPitch` | balanço do corpo |
| `headNod` | cabeçada na corrida |
| `tailSway` | rabo |
| `idle` | respiração e rabo parado |

## Observações
- Foi escrito sem poder abrir o jogo: os valores de amplitude e tempo são um ponto de partida. Se algo parecer exagerado, ajuste no JSON.
- Pacotes que trocam o modelo do cavalo (Fresh Animations/EMF e similares) ou outros mods de animação de cavalo podem sobrepor este mod.
- Se o jogo fechar ao abrir, o motivo está em `logs/latest.log`. O ponto mais provável é o nome de algum campo do modelo do cavalo ter mudado na 26.3 (arquivo `AbstractEquineModelMixin.java`).
