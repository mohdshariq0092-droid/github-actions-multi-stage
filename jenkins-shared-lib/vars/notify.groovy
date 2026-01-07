def call(Map config) {

    def status = config.status
    def app = config.app

    def color = status == 'SUCCESS' ? 'good' : 'danger'

    slackSend(
        color: color,
        message: "*${status}* — ${app}\nJob: ${env.JOB_NAME}\nBuild: ${env.BUILD_URL}"
    )
}
