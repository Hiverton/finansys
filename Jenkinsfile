pipeline {

    agent any
  
    stages {

       stage('NPM Setup') {
          steps {
             sh 'npm install'
         }
       }

       stage('Build') {
          steps {
             sh 'npm build'
             
          }
       }
       
       stage('Deploy') {
          steps {
             sh 'npm start'
             
          }
       }
   }
}
