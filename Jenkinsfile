pipeline {

  agent any
    stages {
      timeout(time: 1, unit: 'HOURS') {
         stage('NPM Setup') {
            steps {
               bat 'npm install'
           }
         }

         stage('Build') {
            steps {
               bat 'npm build'

            }
         }

         stage('Deploy') {
            steps {
               bat 'npm start'

            }
         }
     }
  }
}
