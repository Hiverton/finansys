pipeline {

  agent any
    stages {
      
         stage('NPM Setup') {
            steps {
               bat 'npm install --no-optional'
           }
         }

         stage('Build') {
            steps {
               bat 'ng build'

            }
         }

         stage('Deploy') {
            steps {
               bat 'npm start'

            }
         }
     }
  
}
