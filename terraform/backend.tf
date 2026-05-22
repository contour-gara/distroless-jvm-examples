terraform {
  backend "s3" {
    bucket = "distroless-jvm-samples-tfstate"
    key    = "terraform.tfstate"
    region = "ap-northeast-1"
  }
}
