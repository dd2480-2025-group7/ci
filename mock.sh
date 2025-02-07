#!/bin/bash

# define the JSON payload in a separate file
JSON_FILE="payload.json"

# write the JSON payload to a file
# (update it later if you want it to use another commit)
cat >"$JSON_FILE" <<EOL
{
  "ref": "refs/heads/continued-work",
  "before": "9fdd5be7a8bf80f2fe79da5a28272117f2b3492b",
  "after": "0000000000000000000000000000000000000000",
  "repository": {
    "id": 927046446,
    "node_id": "R_kgDON0GbLg",
    "name": "ci",
    "full_name": "dd2480-2025-group7/ci",
    "private": false,
    "owner": {
      "name": "dd2480-2025-group7",
      "email": null,
      "login": "dd2480-2025-group7",
      "id": 198080145,
      "node_id": "O_kgDOC852kQ",
      "avatar_url": "https://avatars.githubusercontent.com/u/198080145?v=4",
      "url": "https://api.github.com/users/dd2480-2025-group7",
      "html_url": "https://github.com/dd2480-2025-group7",
      "type": "Organization",
      "site_admin": false
    },
    "html_url": "https://github.com/dd2480-2025-group7/ci",
    "description": "DD2480 Assignment #2: CI, Group 7 (2025)",
    "fork": false,
    "url": "https://github.com/dd2480-2025-group7/ci",
    "created_at": 1738663750,
    "updated_at": "2025-02-06T14:32:49Z",
    "pushed_at": 1738917584,
    "git_url": "git://github.com/dd2480-2025-group7/ci.git",
    "ssh_url": "git@github.com:dd2480-2025-group7/ci.git",
    "clone_url": "https://github.com/dd2480-2025-group7/ci.git",
    "language": "Java",
    "has_issues": true,
    "open_issues_count": 9,
    "visibility": "public",
    "default_branch": "main"
  },
  "pusher": {
    "name": "vilhelmprytz",
    "email": "vilhelm@prytznet.se"
  },
  "organization": {
    "login": "dd2480-2025-group7",
    "id": 198080145,
    "avatar_url": "https://avatars.githubusercontent.com/u/198080145?v=4",
    "description": "DD2480 Group 7, 2025"
  },
  "sender": {
    "login": "vilhelmprytz",
    "id": 15460993,
    "avatar_url": "https://avatars.githubusercontent.com/u/15460993?v=4",
    "html_url": "https://github.com/vilhelmprytz"
  },
  "created": false,
  "deleted": true,
  "forced": false,
  "base_ref": null,
  "compare": "https://github.com/dd2480-2025-group7/ci/compare/9fdd5be7a8bf...000000000000",
  "commits": [],
  "head_commit": null
}
EOL

# send the POST request
curl -X POST "http://localhost:8080/webhook" \
  -H "Accept: */*" \
  -H "Content-Type: application/json" \
  -H "User-Agent: GitHub-Hookshot/cc99cca" \
  -H "X-GitHub-Delivery: 14a8a182-e52f-11ef-80e5-717dbe57df22" \
  -H "X-GitHub-Event: push" \
  -H "X-GitHub-Hook-ID: 528527785" \
  -H "X-GitHub-Hook-Installation-Target-ID: 927046446" \
  -H "X-GitHub-Hook-Installation-Target-Type: repository" \
  --data "@$JSON_FILE"

# remove the JSON file
rm "$JSON_FILE"
