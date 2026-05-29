# jvm x distroless

## プロポーザルの変更点

Java のバージョンが 21 -> 25

## 導入

### Docker はローカルでもクラウドでも環境をそろえられる

12 factors app

### JVM のイメージは重い

環境構築時の pull 時間
新規開発時の push 時間
JVM の理念と被ってる

### 計量イメージを使って問題解決

distroless を使おう

## distroless の Java での使い方

### corretto

基本的な Java アプリの Dockerfile

### distroless

JVM の種類
JVM 入り distroless を使う場合の Dockerfile
`CMD ["app.jar"]` の理由

### distroless x custom JRE

custom JRE
BOOT-INF
Dockerfile

### buildpacks x distroless x custom JRE

runImage
environment.BP_JVM_JLINK_ENABLE
environment.BP_JVM_JLINK_ARG

## 比較

### サイズ

### コンテナ実行環境での起動時間

#### コンテナ起動時間とイメージサイズの関係

pull 時間 + アプリ起動次男 = コンテナ起動時間

#### 実験手法

ECR + ECS Express
デプロイ方式

#### 実験結果

#### 考察

Dockerfile 形式同士の比較から、サイズは起動時間に寄与する
buildpacks の結果から、サイズだけが起動時間に寄与する訳ではない

## distroless を使用する場合にやってほしいこと

### ローカルから docker で起動しておく

### コンテナレベルのブラックボックステスト

### リモートデバッグ可能

custom JRE を使用している場合は、jdk.jdwp.agent を追加
compose.yaml で 5005 ポートの解放と JAVA_TOOL_OPTIONS の環境変数を設定

### ローカルのヘルスチェックが難しい

Dockerfile や compose.yaml のヘルスチェックはコンテナ内でコマンドを実行する
curl がないためヘルスチェックができない
クラウドの場合は問題ないことが多いので気にしていない

## まとめ

- イメージサイズの軽量化はこれらの時間短縮に寄与する
  - ローカル環境での初回 pull
  - コンテナレジストリへの初回 push
  - コンテナ実行環境での起動時間
- 起動時間の短縮は、サイズだけではなくコンテナ構造も寄与する
- コンテナイメージを工夫する場合、常に distroless を使ってアプリを起動できるようにしておく
