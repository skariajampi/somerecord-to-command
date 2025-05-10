pipeline{
    agent any
    environment{
    VERSION =   '1.0.0-SNAPSHOT'
    }
    stages{
        stage('Tools'){
            steps{
                sh '''
                git version
                java -version
                mvn -version
                '''
            }
        }
        stage('Build'){
            steps{
                dir('.'){
                sh '''
                                                echo "Executing Tests..."
                                                mvn clean test
                                                '''
                }

            }
        }
        stage('Package'){
        steps{
                        dir('.') {
                        sh '''
                                        echo "Packaging version ${VERSION}..."
                                        mvn package
                                        '''
                              }
                    }
        }
        stage('Deploy'){
                steps{
                                dir('.') {
                                sh '''
                                                echo "Packaging version ${VERSION}..."
                                                mvn -DskipITs deploy
                                                '''
                                      }
                            }
                }
    }
}