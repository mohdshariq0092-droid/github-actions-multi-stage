def call(Map config = [:]) {

    def appName = config.appName ?: 'python-app'
    def coverageThreshold = config.coverageThreshold ?: 80
    def agentLabel = config.agent ?: 'docker-python'

    pipeline {
        agent { label agentLabel }

        options {
            timestamps()
            buildDiscarder(logRotator(numToKeepStr: '20'))
        }

        environment {
            APP_NAME = appName
            VENV = ".venv"
        }

        stages {

            stage('Checkout') {
                steps {
                    checkout scm
                }
            }

            stage('Setup Python Env') {
                steps {
                    sh '''
                        python3 -m venv ${VENV}
                        . ${VENV}/bin/activate
                        pip install --upgrade pip
                        pip install -r requirements.txt
                    '''
                }
            }

            stage('Lint (Static Analysis)') {
                steps {
                    sh '''
                        . ${VENV}/bin/activate
                        flake8 calculator --count --select=E9,F63,F7,F82 --show-source --statistics
                    '''
                }
            }

            stage('Unit Tests') {
                steps {
                    sh '''
                        . ${VENV}/bin/activate
                        pytest --cov=calculator --cov-report=term --cov-report=xml
                    '''
                }
            }

            stage('Coverage Quality Gate') {
                steps {
                    qualityGate(
                        tool: 'pytest-cov',
                        threshold: coverageThreshold
                    )
                }
            }
        }

        post {
            success {
                notify(status: 'SUCCESS', app: appName)
            }
            failure {
                notify(status: 'FAILURE', app: appName)
            }
            always {
                archiveArtifacts artifacts: '**/coverage.xml', fingerprint: true
            }
        }
    }
}
