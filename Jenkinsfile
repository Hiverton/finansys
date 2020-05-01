pipeline {

  agent any
  timeout(time: 1, unit: 'HOURS') {
      stages {

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
