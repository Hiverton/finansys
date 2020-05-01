pipeline {

    agent any
    timeout(200) {
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
