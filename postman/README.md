# Postman API Collection & Newman Execution

This directory contains the exported Postman Collection and Environment files for API exploration, manual verification, and Newman CLI integration.

## Files

- `ATS_API_Collection.json`: Postman Collection v2.1.0 containing authentication, CRUD operations (GET, POST, PUT, DELETE), positive assertions, and negative scenario checks.
- `ATS_Environment.json`: Environment variable configuration for `baseUrl` (`https://reqres.in`).

## Running with Newman CLI

To run the Postman collection headlessly via Newman CLI:

### 1. Install Newman Globally (Node.js required)
```bash
npm install -g newman
```

### 2. Execute Collection via Newman
```bash
newman run postman/ATS_API_Collection.json -e postman/ATS_Environment.json
```

### 3. Execute Collection via npx (No Global Install)
```bash
npx newman run postman/ATS_API_Collection.json -e postman/ATS_Environment.json
```
