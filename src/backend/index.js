// backend/index.js
const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const { spawn } = require('child_process');

const app = express();
const port = 20501;

app.use(bodyParser.json());
app.use(cors());

app.post('/classify', (req, res) => {
    const codeInput = req.body.code;

    if (!codeInput) {
        return res.status(400).json({ error: 'No code provided' });
    }

    const classpath = '../parser/build/classes:/Users/andy/workplace/code-classifier/src/parser/target/classes:/Users/andy/.m2/repository/com/github/javaparser/javaparser-symbol-solver-core/3.26.2/javaparser-symbol-solver-core-3.26.2.jar:/Users/andy/.m2/repository/com/github/javaparser/javaparser-core/3.26.2/javaparser-core-3.26.2.jar:/Users/andy/.m2/repository/org/javassist/javassist/3.30.2-GA/javassist-3.30.2-GA.jar:/Users/andy/.m2/repository/com/google/guava/guava/32.1.2-jre/guava-32.1.2-jre.jar:/Users/andy/.m2/repository/com/google/guava/failureaccess/1.0.1/failureaccess-1.0.1.jar:/Users/andy/.m2/repository/com/google/guava/listenablefuture/9999.0-empty-to-avoid-conflict-with-guava/listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar:/Users/andy/.m2/repository/com/google/code/findbugs/jsr305/3.0.2/jsr305-3.0.2.jar:/Users/andy/.m2/repository/org/checkerframework/checker-qual/3.33.0/checker-qual-3.33.0.jar:/Users/andy/.m2/repository/com/google/errorprone/error_prone_annotations/2.18.0/error_prone_annotations-2.18.0.jar:/Users/andy/.m2/repository/com/google/j2objc/j2objc-annotations/2.8/j2objc-annotations-2.8.jar'
    // Invoke the Java program
    const javaProcess = spawn('java', ['-cp', classpath, 'me.andystanciu.parser.demo.Demo', codeInput]);

    let outputData = '';
    let errorData = '';

    javaProcess.stdout.on('data', (data) => {
        outputData += data.toString();
    });

    javaProcess.stderr.on('data', (data) => {
        errorData += data.toString();
    });

    javaProcess.on('close', (code) => {
        if (code !== 0) {
            console.error(`Java process exited with code ${code}`);
            console.error(`Error output: ${errorData}`);
            return res.status(500).json({ error: 'Error processing code' });
        }
        res.json({ problemName: outputData.trim() });
    });

    // Send the code input to the Java program
    javaProcess.stdin.write(codeInput);
    javaProcess.stdin.end();
});

app.listen(port, () => {
    console.log(`Backend server is running on http://localhost:${port}`);
});
