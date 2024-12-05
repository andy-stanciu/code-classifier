// src/App.js
import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import debounce from 'lodash.debounce';
import Editor from '@monaco-editor/react';
import { Box, Typography, CircularProgress, Paper } from '@mui/material';

function App() {
  const [code, setCode] = useState('');
  const [problemNames, setProblemNames] = useState([]);
  const [loading, setLoading] = useState(false);

  const classifyProblem = async (codeInput) => {
    if (!codeInput.trim()) {
      setProblemNames([]);
      return;
    }

    setLoading(true);
    try {
      const response = await axios.post('http://localhost:20501/classify', { code: codeInput });
      const namesArray = response.data.problemName
        .split('$')
        .filter((name) => name.trim() !== '');
      setProblemNames(namesArray);
    } catch (error) {
      console.error('Error classifying problem:', error);
      setProblemNames(['Error classifying problem.']);
    } finally {
      setLoading(false);
    }
  };

  // Debounce the API call
  const debouncedClassifyProblem = useCallback(debounce(classifyProblem, 500), []);

  useEffect(() => {
    debouncedClassifyProblem(code);
    // Cleanup function
    return debouncedClassifyProblem.cancel;
  }, [code, debouncedClassifyProblem]);

  // Handle editor change
  const handleEditorChange = (value) => {
    if (value !== undefined) {
      setCode(value);
    }
  };

  return (
    <Box sx={{ padding: 4, backgroundColor: '#f5f5f5', minHeight: '100vh' }}>
      <Typography variant="h2" component="h1" gutterBottom>
        LeetCode Problem Classifier
      </Typography>
      <Typography variant="h6" component="h1" gutterBottom>
        Andy Stanciu & Rich Chen
      </Typography>
      <Paper elevation={3} sx={{ padding: 2, marginBottom: 4 }}>
        <Editor
          height="450px"
          defaultLanguage="java"
          value={code}
          onChange={handleEditorChange}
          options={{
            tabSize: 4,
            fontSize: 14,
            automaticLayout: true,
          }}
        />
      </Paper>
      <Box>
        <Typography variant="h5" component="h2" gutterBottom>
          This code is attempting to solve...
        </Typography>
        {loading ? (
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <CircularProgress size={24} sx={{ marginRight: 2 }} />
            <Typography>Classifying problem...</Typography>
          </Box>
        ) : problemNames.length > 0 ? (
          <Box>
            <Typography variant="h6" component="h3" sx={{ marginBottom: 1 }}>
              {problemNames[0]}
            </Typography>
            {problemNames.slice(1).map((name, index) => (
              <Typography key={index} variant="body1" sx={{ marginBottom: 0.5 }}>
                {name}
              </Typography>
            ))}
          </Box>
        ) : (
          <Typography>No problem provided.</Typography>
        )}
      </Box>
    </Box>
  );
}

export default App;
