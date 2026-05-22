locals {
  application_id = "distroless-jvm-samples"
  service_names = toset([
    "corretto",
    "distroless",
    "distroless-custom-jre",
    "distroless-custom-jre-buildpack",
  ])
}
