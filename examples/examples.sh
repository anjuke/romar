#!/bin/sh;
curl 'localhost:8080/users/1/recommendations';echo
curl -H "Content-Type: application/json"  -H "Accept: application/json" -X PUT -d '{"value":1}' 'localhost:8080/preferences/1/1';echo
curl -H "Accept: application/json" 'localhost:8080/users/1/recommendations';echo
curl -X POST 'localhost:8080/commit';echo
curl -H "Accept: application/json" 'localhost:8080/users/1/recommendations';echo
curl -H "Content-Type: application/json"  -H "Accept: application/json" -X PUT -d '{"value":1}' 'localhost:8080/preferences/1/2';echo
curl -H "Content-Type: application/json"  -H "Accept: application/json" -X PUT -d '{"value":1}' 'localhost:8080/preferences/2/1';echo
curl -H "Content-Type: application/json"  -H "Accept: application/json" -X PUT -d '{"value":1}' 'localhost:8080/preferences/2/2';echo
curl -H "Content-Type: application/json"  -H "Accept: application/json" -X PUT -d '{"value":1}' 'localhost:8080/preferences/2/3';echo
curl -H "Content-Type: application/json"  -H "Accept: application/json" -X PUT -d '{"value":1}' 'localhost:8080/preferences/2/4';echo
curl -H "Content-Type: application/json"  -H "Accept: application/json" -X PUT -d '{"value":1}' 'localhost:8080/preferences/2/5';echo
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
curl -H "Content-Type: application/json"  -H "Accept: application/json" -X PUT -d '[[1,2,3],[2,3,1],[4,1,3]]' 'localhost:8080/preferences';echo

