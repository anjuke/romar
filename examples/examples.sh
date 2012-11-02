#!/bin/sh;
curl 'localhost:8080/users/1/recommendations';echo
curl -H "Content-Type: application/json"  -H "Accept: application/json" -X PUT -d '{"user":1,"item":1,"value":1}' 'localhost:8080/preferences';echo
curl -H "Accept: application/json" 'localhost:8080/users/1/recommendations';echo
curl -X POST 'localhost:8080/commit';echo
curl -H "Accept: application/json" 'localhost:8080/users/1/recommendations';echo
curl -H "Content-Type: application/json"  -H "Accept: application/json" -X PUT -d '{"user":1,"item":2,"value":1}' 'localhost:8080/preferences';echo
curl -H "Content-Type: application/json"  -H "Accept: application/json" -X PUT -d '{"user":2,"item":1,"value":1}' 'localhost:8080/preferences';echo
curl -H "Content-Type: application/json"  -H "Accept: application/json" -X PUT -d '{"user":2,"item":2,"value":1}' 'localhost:8080/preferences';echo
curl -H "Content-Type: application/json"  -H "Accept: application/json" -X PUT -d '{"user":2,"item":3,"value":1}' 'localhost:8080/preferences';echo
curl -H "Content-Type: application/json"  -H "Accept: application/json" -X PUT -d '{"user":2,"item":4,"value":1}' 'localhost:8080/preferences';echo
curl -H "Content-Type: application/json"  -H "Accept: application/json" -X PUT -d '{"user":2,"item":5,"value":1}' 'localhost:8080/preferences';echo
curl -X POST 'localhost:8080/commit';echo
curl -H "Accept: application/json" 'localhost:8080/users/1/recommendations';echo
curl -H "Accept: application/json" 'localhost:8080/items/similars?item=1&item=2';echo
curl -X DELETE 'localhost:8080/preferences/2/3';echo
curl -X POST 'localhost:8080/commit';echo
curl -H "Accept: application/json" 'localhost:8080/users/1/recommendations';echo
curl -X POST 'localhost:8080/optimize';echo
curl -X POST 'localhost:8080/optimize';echo
curl -X POST 'localhost:8080/commit';echo
curl -X POST 'localhost:8080/optimize';echo
curl -X POST 'localhost:8080/optimize';echo
curl -H "Accept: application/json" 'localhost:8080/preferences/1/5';echo
curl -X DELETE 'localhost:8080/items/4';echo
curl -X POST 'localhost:8080/commit';echo
curl -H "Accept: application/json" 'localhost:8080/users/1/recommendations';echo
curl -H "Accept: application/json" 'localhost:8080/preferences/1/5';echo
curl -H "Accept: application/json" 'localhost:8080/users/1/similars';echo

