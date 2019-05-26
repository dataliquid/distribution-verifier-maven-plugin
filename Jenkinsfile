pipeline {
 agent {
  node {
   label 'master'
  }
 }
 tools {
  maven 'maven-3'
 }
 stages {
  stage("Prepare WS") {
   steps {
    cleanWs()
    checkout scm
   }
  }
  stage("Build") {
   parallel {
    stage('Build') {
     when {
      not {
       branch "master"
      }
     }
     steps {
      sh "mvn -T 2C compile install -DskipTests"
     }
    }
    stage('Tests') {
     steps {
      sh "mvn -T 2C test-compile test -DfailIfNoTests=false"
     }
     post {
      always {
       step([$class: 'JUnitResultArchiver', allowEmptyResults: true, testResults: '**/target/surefire-reports/TEST-*.xml'])
      }
     }
    }
   }
  }
  stage("Deploy") {
   steps {
    sh "mvn deploy -DskipTests"
   }
  }
 }
}