pipeline {

    agent any

    environment {
        PATH='/usr/local/bin:/usr/bin:/bin'
	}

    stages {

       stage('NPM Setup') {
          steps {
             sh 'npm install'
         }
       }

       stage('Build') {
          steps {
             sh 'ng build'
             
          }
       }
       
       stage('Deploy') {
          steps {
             sh 'npm start'
             
          }
       }
   }
}
