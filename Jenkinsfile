def affectedServices = []

pipeline {
    agent any

    environment {
        GITHUB_TOKEN = credentials('github-PAT') 
    }

    stages {
        stage('Check if PR to main') {
            when {
                expression { return env.CHANGE_ID && env.CHANGE_TARGET == 'main' }
            }
            steps {
                echo "Pull request vào main, tiếp tục kiểm tra!"
            }
        }

        stage('Determine changed services') {
            steps {
                script {
                    def changedFiles
                    if (env.CHANGE_TARGET) {  
                        changedFiles = sh(returnStdout: true, script: "git diff --name-only origin/${env.CHANGE_TARGET}...HEAD").trim().split('\n')
                    } else {  
                        changedFiles = sh(returnStdout: true, script: "git diff --name-only HEAD^ HEAD").trim().split('\n')
                    }

                    affectedServices = changedFiles.findAll { file ->
                        file.startsWith('spring-petclinic-')
                    }.collect { file ->
                        return file.split('/')[0]
                    }.unique()

                    echo "Affected services: ${affectedServices}"
                }
            }
        }

        stage('Test affected services') {
            when {
                expression { return affectedServices?.size() > 0 }
            }
            steps {
                script {
                    for (service in affectedServices) {
                        echo "Testing ${service} ..."
                        sh "./mvnw clean test -f ${service}/pom.xml"

                        sh "ls -al ${service}/target/surefire-reports/"
                        junit "${service}/target/surefire-reports/*.xml"

                        jacoco execPattern: "${service}/target/*.exec", classPattern: "${service}/target/classes", sourcePattern: "${service}/src/main/java", inclusionPattern: "**/*.class", changeBuildStatus: true

                        def coverage = getCoveragePercentage("${service}/target/site/jacoco/jacoco.csv")

                        echo "Coverage for ${service}: ${coverage}%"

                        if (coverage < 70) {
                            error "Coverage is below 70% for ${service}!"
                        }
                        
                        echo "${service} test completed."
                    }

                }
            }
        }

        stage('Build affected services') {
            when {
                expression { return affectedServices?.size() > 0 }
            }
            steps {
                script {
                    for (service in affectedServices) {
                        echo "Building ${service} ..."
                        sh "./mvnw clean install -f ${service}/pom.xml -DskipTests"
                        echo "${service} build completed."
                    }
                }
            }
        }

        stage('Test') {
            steps {
                echo "Testing.."
                script {
                    def statusFile = 'status.txt'
                    def statusContent = readFile(statusFile).trim() // Read file content and remove any surrounding whitespace

                    if (statusContent == 'a') {
                        echo "Content is 'a', proceeding with echo."
                    } else if (statusContent == 'd') {
                        error "Content is 'd', raising error."
                    } else {
                        echo "Content is neither 'a' nor 'd'."
                    }
                }
            }
        }

    }

    post {
        always {
            echo 'Pipeline completed!'
        }
        success {
            script {
                // Ensure GIT_COMMIT is set and not null
                if (env.GIT_COMMIT) {
                    def response = sh(script: """
                        curl -X POST --user ${GITHUB_TOKEN} \
                            -H "Accept: application/vnd.github.v3+json" \
                            https://api.github.com/repos/nqthangcs/aaa/statuses/${env.GIT_COMMIT} \
                            -d '{"state": "success", "description": "Tests passed!", "context": "jenkins-ci"}'
                    """, returnStdout: true).trim()

                    if (response.contains("error")) {
                        error "GitHub status update failed. Response: ${response}"
                    }
                } else {
                    echo "GIT_COMMIT is not set. Skipping GitHub status update."
                }
            }
        }

        failure {
            script {
                // Ensure GIT_COMMIT is set and not null
                if (env.GIT_COMMIT) {
                    def response = sh(script: """
                        curl -X POST --user ${GITHUB_TOKEN} \
                            -H "Accept: application/vnd.github.v3+json" \
                            https://api.github.com/repos/nqthangcs/aaa/statuses/${env.GIT_COMMIT} \
                            -d '{"state": "failure", "description": "Tests failed!", "context": "jenkins-ci"}'
                    """, returnStdout: true).trim()

                    if (response.contains("error")) {
                        error "GitHub status update failed. Response: ${response}"
                    }
                } else {
                    echo "GIT_COMMIT is not set. Skipping GitHub status update."
                }
            }
        }
    }
}


double getCoveragePercentage(String filepath) {
    def fileContents = readFile(filepath)
    def totalMissed = 0
    def totalCovered = 0

    fileContents.split('\n').eachWithIndex { line, index ->
        if (index == 0) return 

        def columns = line.split(",")
        totalMissed += columns[3].toInteger() + columns[5].toInteger() + columns[7].toInteger() + columns[9].toInteger() + columns[11].toInteger()
        totalCovered += columns[4].toInteger() + columns[6].toInteger() + columns[8].toInteger() + columns[10].toInteger() + columns[12].toInteger()
    }

    return (totalCovered / (totalCovered + totalMissed)) * 100
}
