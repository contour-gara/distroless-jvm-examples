# Java × distroless で軽量なコンテナイメージを

## プロポーザルの変更点

- Java のバージョンが 21 -> 25

## アジェンダ

1. Distroless を使うモチベーション
2. Java アプリを Distroless で動かす
3. コンテナイメージのサイズと起動速度の比較
4. Java アプリを Distroless で動かす際の注意点

[この発表のメモ](https://github.com/contour-gara/distroless-jvm-examples/blob/main/slide/slide.md)

## Distroless を使うモチベーション

### なぜコンテナアプリケーション

- [The Twelve Factor App](https://12factor.net/ja/)
- ローカルでもクラウドでも同じものが動く

### JVM のイメージは重い

- docker イメージのサイズ
  - [amazoncorretto](https://hub.docker.com/layers/library/amazoncorretto/latest/images/sha256-4b1c7dcbc66e17910ed111d9731760921c55e90e432dff7a7ae7f44a2da32ee3): 132.64 MB
  - [eclipse-temurin](https://hub.docker.com/layers/library/eclipse-temurin/latest/images/sha256-98c06ec4ce7915e354d6845276c6901bd5ab075d5fcac0224fdb31b8314329b1): 143.42 MB
  - 参考:
    - [debian](https://hub.docker.com/layers/library/debian/latest/images/sha256-2477d9ee0ead4370c778ce3aa42258a0b07684d1a84ded8f4af518383fbc3f2d): 47.03 MB
- 100 MB 超えのイメージにファット jar が乗る
- 環境構築時の pull 時間が長い
- 新規開発時の push 時間が長い
- JVM の理念と被ってる
  - Write Once, Run Anywhere
  - 抽象化レイヤーが二重にかかってる
  - ランタイムが別である

### 軽量イメージを使って問題解決

- [debian-slim](https://hub.docker.com/_/debian/tags?name=slim)
- [Alpine Linux](https://www.alpinelinux.org/)
- [Distroless](https://github.com/GoogleContainerTools/distroless)
  - Google 管理
  - シェルがないため攻撃に強い
  - Java で Distroless を使っている話をあまり聞かないので、今回のテーマに

## Java アプリを Distroless で動かす

### サンプルアプリについて

- [リポジトリ](https://github.com/contour-gara/distroless-jvm-examples)
- Kotlin, Java25, Spring Boot 4.0.6
- ルートエンドポイントに GET すると "Hello World!" が返る

### corretto

- [Dockerfile](../app/Dockerfile_corretto)
- 基本的な Java アプリの Dockerfile

### Distroless

- [Dockerfile](../app/Dockerfile_distroless)
- JVM は Temurin
  - https://github.com/GoogleContainerTools/distroless/tree/main/java
- `CMD ["app.jar"]` の理由
  - Entrypoint に `java -jar` が指定されている
    ```
    $ docker inspect gcr.io/distroless/java25-debian13:nonroot
    [
      {
        "Id": "sha256:c0d379ff54ea6d61f3f35736e8fdc66c91fb96f645a86a6ba2530a45d95b8841",
        "RepoTags": [
          "gcr.io/distroless/java25-debian13:nonroot"
        ],
        "RepoDigests": [
          "gcr.io/distroless/java25-debian13@sha256:c0d379ff54ea6d61f3f35736e8fdc66c91fb96f645a86a6ba2530a45d95b8841"
        ],
        "Created": "1970-01-01T00:00:00Z",
        "Config": {
          "User": "65532",
          "Env": [
            "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
            "SSL_CERT_FILE=/etc/ssl/certs/ca-certificates.crt",
            "LANG=C.UTF-8",
            "JAVA_VERSION=25.0.3"
          ],
          "Entrypoint": [
            "/usr/bin/java",
            "-jar"
          ],
          "WorkingDir": "/home/nonroot"
        },
        ~~~
        省略
        ~~~
      }
    ]
    ```

### Distroless x カスタム JRE

- [Dockerfile](../app/Dockerfile_distroless_custom_jre)
- アプリ実行に必要なモジュールのみを集めた JRE
- Spring Boot アプリの依存モジュールの調べ方
  - 割愛
  - [tasks.jdeps 参照](../mise.toml)

### Buildpacks x Distroless x カスタム JRE

- [bootBuildImage の設定](../app/build.gradle.kts)
- runImage に distroless/java-base
- 環境変数 BP_JVM_JLINK_ENABLE, BP_JVM_JLINK_ARG を設定することでカスタム JRE 使用

## コンテナイメージのサイズと起動速度の比較

### サイズの比較

圧縮後サイズ

| Image                           |      Size |
|:--------------------------------|----------:|
| corretto                        | 265.43 MB |
| distroless                      |  94.62 MB |
| distroless-custom-jre           |  72.79 MB |
| distroless-custom-jre-buildpack |  87.92 MB |

### コンテナ実行環境での起動時間の比較

#### コンテナ起動時間とイメージサイズの関係

- pull 時間 + アプリ起動時間 = コンテナ起動時間

#### 実験手法

- ECR + ECS Express
- デプロイ方式: [カナリアデプロイ](https://docs.aws.amazon.com/ja_jp/AmazonECS/latest/developerguide/express-service-update-full.html)
  - ベイク時間: 合計 6 分

#### 実験結果

![箱ひげ図](../notebook/box.png)

- corretto を使ったイメージが遅かった
- buildpacks を利用して作ったイメージが速かった

#### 考察

- Dockerfile 形式同士の比較から、軽量なほど起動時間が速い
- Buildpacks の結果から、サイズだけが起動時間に影響している訳ではない
  - Dockerfile で作ったイメージと構造が違うが今回は割愛

## Java アプリを Distroless で動かす際の注意点

### ローカルから docker で起動しておく

- ローカルでもクラウドでも同じものを実行する
  - The Twelve Factor App
- compose.yaml を書いておく
- アプリ起動に必要な環境変数もまとめられる
- build jar -> compose up or compose up --build

### コンテナレベルのブラックボックステスト

- アプリの異常だけでなくコンテナの異常を検知できる
- docker compose プラグインでテスト実行前の compose up と実行後の compose down を自動化
  - [build.gradle.kts](../integration-test/build.gradle.kts)

### リモートデバッグ

- ローカルでコンテナを実行してもデバッグが可能
- custom JRE を使用している場合は、jdk.jdwp.agent を追加
- compose.yaml で 5005 ポートの解放と JAVA_TOOL_OPTIONS の環境変数を設定

### ローカルのヘルスチェックが難しい

- Dockerfile や compose.yaml のヘルスチェックはコンテナ内でコマンドを実行する
- curl がないためヘルスチェックができない
- クラウドの場合は問題ないことが多いので気にしていない
  - ALB/NLB の HTTP ヘルスチェックを使うため

## まとめ

- イメージサイズの軽量化はこれらの時間短縮に寄与する
  - ローカル環境での初回 pull
  - コンテナレジストリへの初回 push
  - コンテナ実行環境での起動時間
- 起動時間の短縮は、サイズだけではなくコンテナ構造も影響する
- コンテナイメージを工夫する場合、常にコンテナでアプリを起動するようにする
