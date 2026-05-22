resource "aws_ecs_cluster" "this" {
  name = local.application_id
}

resource "aws_ecs_express_gateway_service" "corretto" {
  execution_role_arn      = aws_iam_role.task_execution.arn
  infrastructure_role_arn = aws_iam_role.infrastructure.arn
  cluster                 = aws_ecs_cluster.this.arn
  health_check_path       = "/actuator/health"
  service_name            = "corretto"

  primary_container {
    image          = "${aws_ecr_repository.app.repository_url}:corretto"
    container_port = 8080
  }

  depends_on = [
    aws_iam_role_policy_attachment.task_execution,
    aws_iam_role_policy_attachment.infrastructure,
  ]
}
