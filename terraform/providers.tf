provider "aws" {
  region = "ap-northeast-1"

  default_tags {
    tags = {
      application-id = local.application_id
    }
  }
}
