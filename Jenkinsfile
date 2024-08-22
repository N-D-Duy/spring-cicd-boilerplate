pipeline {
    agent {
        kubernetes {
            yaml '''
                apiVersion: v1
                kind: Pod
                spec:
                  containers:
                  - name: nodejs
                    image: node:20
                    command:
                    - cat
                    tty: true
                  - name: maven
                    image: maven:3.8.3-openjdk-17
                    command:
                    - cat
                    tty: true
                  - name: kaniko
                    image: gcr.io/kaniko-project/executor:debug
                    command:
                    - sleep
                    args:
                    - 9999999
                    tty: true
                    volumeMounts:
                    - name: kaniko-secret
                      mountPath: /kaniko/.docker
                  volumes:
                  - name: kaniko-secret
                    secret:
                        secretName: dockercred
                        items:
                        - key: .dockerconfigjson
                          path: config.json
            '''
            retries 2
        }
    }

    environment {
        IMAGE_NAME = 'duynguyen03/duy-spring-user'
        CONTEXT = 'duy-spring'
        VERSION = ''
    }
    stages {
        stage('Build and Test') {
            steps {
                container('maven') {
                    sh '''
                        mvn clean package
                    '''
                }
            }
        }
        stage('Code Analysis') {
            steps {
                container('maven') {
                    withSonarQubeEnv('sonarqube-jenkins') {
                        sh 'mvn sonar:sonar'
                    }
                }
            }
        }

        stage('Semantic release') {
            when {
                branch 'main'
            }
            steps {
                container('nodejs') {
                    withCredentials([string(credentialsId: 'GH_TOKEN', variable: 'GH_TOKEN')]) {
                        script {
                            sh '''
                                git config --global --add safe.directory /home/jenkins/agent/workspace/docker-image
                                npm install -g semantic-release @semantic-release/commit-analyzer @semantic-release/release-notes-generator @semantic-release/changelog @semantic-release/npm @semantic-release/git @semantic-release/github
                                git config --global user.email "ct060311@actvn.edu.vn"
                                git config --global user.name "N-D-Duy"
                                
                                # Run semantic-release and capture the output
                                RELEASE_OUTPUT=$(npx semantic-release --no-ci 2>&1)

                                # Extract the version number from the output
                                VERSION=$(echo "$RELEASE_OUTPUT" | grep "Prepared Git release:" | awk -F ': ' '{print $2}' | sed 's/v//')
                                echo "VERSION=${VERSION}" > release_version.env
                                
                            '''
                            // Load the version into environment
                            def releaseVersion = sh(script: "cat release_version.env | grep 'VERSION=' | sed 's/VERSION=//'", returnStdout: true).trim()
                            echo "Release version is: ${releaseVersion}"
                            env.VERSION = releaseVersion
                        }
                    }
                }
            }
        }

        stage('Build Push Docker Image') {
            when {
                branch 'main'
            }
            steps {
                container('kaniko') {
                    sh '''
                        /kaniko/executor --context `pwd` \
                                    --dockerfile `pwd`/Dockerfile \
                                    --destination ${IMAGE_NAME}:${RELEASE_VERSION}
                    '''
                }
            }
        }

        stage('Update Deployment YAML') {
            when {
                branch 'main'
            }
            steps {
                container('nodejs') {
                    echo "${VERSION}"
                    sh """
                        # Update the image tag in deployment.yaml
                        sed -i 's|image: ${IMAGE_NAME}:.*|image: ${IMAGE_NAME}:${VERSION}|g' k8s/deployment.yaml
    
                        # Commit and push the changes
                        git add k8s/deployment.yaml
                        git commit -m "Update version to ${VERSION}"
                    """
                    withCredentials([string(credentialsId: 'github', gitToolName: 'Default')]) {
                        sh '''
                            git push https://github.com/N-D-Duy/duy-spring main
                        '''
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }

        success {
            script {
                echo 'Success'
            }
        }

        failure {
            script {
                echo 'Failure'
            }
        }
    }
}