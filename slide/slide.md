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

### 軽量イメージを使って問題解決

- [debian-slim](https://hub.docker.com/_/debian/tags?name=slim)
- [Alpine Linux](https://www.alpinelinux.org/)
- [Distroless](https://github.com/GoogleContainerTools/distroless)

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
        "Architecture": "amd64",
        "Os": "linux",
        "Size": 73189305,
        "RootFS": {
          "Type": "layers",
          "Layers": [
            "sha256:82c60ccaf916322916d16bcdb4223f93acc1f68e2087dba4ddf64990b1dc27fb",
            "sha256:621c35e751a51a9a9dc3e80aa0b7fe8be2a93402ea6ccd307d30852cd7776cda",
            "sha256:ac2a91ec876dfaf2145e14b0b43ce6b3ea3d4edb28a0df9d91c52f2efbb8e1a7",
            "sha256:f15316efa9979a44eee43172e640630f60407180eff3d985274befd600bb227d",
            "sha256:275a30dd8ce958b21daa9ad962c6fbc09f98306ee2f486b65c9075dc257b1412",
            "sha256:4d049f83d9cf21d1f5cc0e11deaf36df02790d0e60c1a3829538fb4b61685368",
            "sha256:af5aa97ebe6ce1604747ec1e21af7136ded391bcabe4acef882e718a87c86bcc",
            "sha256:6f1cdceb6a3146f0ccb986521156bef8a422cdbb0863396f7f751f575ba308f4",
            "sha256:bd3cdfae1d3fdd83a2231d608969b38b82349777c2fff9a7c12d54f8ac5c9b38",
            "sha256:4cde6b0bb6f50a5f255eef7b2a42162c661cf776b803225dcac9a659e396bb6b",
            "sha256:ad51d0769d16ba578106a177987dfe3d2e02c1668c852b795b2f6b024068242a",
            "sha256:187cfc6d1e3e8a40a5e64653bcd3239c140807dcf1c09e48021178705a5a6139",
            "sha256:5fd2536c39c0700be8b7b4344e375196da2f126842fd8ede66996a18860a3890",
            "sha256:318bc252656ce5b3c77fc9a13e302bba683f813d4b19c21be6e0ad3acc7adaf1",
            "sha256:e4ba966d7f0527dfe0fcb559e4e18d4da42c4e6beae924719255e0dedb554ed0",
            "sha256:ed866e58bebf5b90b1db471f4eff229d62dd0682a8b3abb727776c425e93f0b6",
            "sha256:29d492a034557b4b338ab6ef33e3f1ecfaaa340ff721aae0776149b59cc7052d",
            "sha256:6c4a78b210237270efb567d7d2542b190c77c20d44adefbc3496d2d085d271e4",
            "sha256:0cd351bd16de499bf614cf9e35cd1b64e61272119b38dbc95e5eae87163c1f7e",
            "sha256:a2195e8a560e78570f0ab99b60ee87df454da67fd8d44b49769d007b2a294d92",
            "sha256:49bdf8e65c465162a9c999342a8e7f6e5ec4a1bfd10d3962474f3df3d7bdd763",
            "sha256:d729025149d33e1372a0056f6a791f963d4508abe5fa430109194e31052f291f",
            "sha256:38253f8df53ddff4e80c63ab44f5f224500b27ff2094bef5a22b191f364b9565",
            "sha256:05d736472d09f08a3b66814899514fdac48489e2f77f07de6f0b88461f8815f4",
            "sha256:6db791322bfcbc72dc54aa46d012ef46a2314819ec9cc28904e9091b4b94e63e",
            "sha256:6e18ad80f3d64a8cbbcd1ff2e8a0d5ce7282cf664e816b86183a59d30a618e8a",
            "sha256:c16b2ec4b1493bad1b1de23d659c899e60abb166bda756d02792f0a03ba54a43",
            "sha256:7db505d90756626f425c6c5468eca565c82f589b144ecaa4f411ad9bbf79e614",
            "sha256:b25b5b4e7b93b4c0b8b9a8fe94063231389989d767c227027755835ffb2be2b8",
            "sha256:953871f68b73a0c29f9268de527a33d56fb21e904f9b3b9a52281ead677a015a",
            "sha256:09953d04270aec63398eaa8ac602a8f0372f107e6d1190ed0216980309d52131",
            "sha256:cce99c5700b92fb6fd47e9f8f4fea8428afe2a25abc7a3cd3635ee633cdbb90a",
            "sha256:57ba5b8d401c4675bf8ee2bf5173f2b1804175b04ac312a8f19611aa5b93d885"
          ]
        },
        "Metadata": {
          "LastTagTime": "2026-05-17T19:00:25.08281261Z"
        },
        "Descriptor": {
          "mediaType": "application/vnd.oci.image.index.v1+json",
          "digest": "sha256:c0d379ff54ea6d61f3f35736e8fdc66c91fb96f645a86a6ba2530a45d95b8841",
          "size": 1493
        },
        "Identity": {
          "Pull": [
            {
              "Repository": "gcr.io/distroless/java25-debian13"
            }
          ]
        }
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
- buildpacks を利用して作ったイメージが早かった

#### 考察

- Dockerfile 形式同士の比較から、軽量なほど起動時間が早い
- Buildpacks の結果から、サイズだけが起動時間に影響している訳ではない

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

## まとめ

- イメージサイズの軽量化はこれらの時間短縮に寄与する
  - ローカル環境での初回 pull
  - コンテナレジストリへの初回 push
  - コンテナ実行環境での起動時間
- 起動時間の短縮は、サイズだけではなくコンテナ構造も寄与する
- コンテナイメージを工夫する場合、常にコンテナでアプリを起動するようにする
