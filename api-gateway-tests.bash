#!/usr/bin/env bash

set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080/api/v1}"

response_body=""
status_code=""

call_api() {
  local method=$1
  local path=$2
  local body=${3:-}
  local response

  if [ -n "$body" ]; then
    response=$(curl -s -w "\n%{http_code}" -X "$method" "$BASE_URL$path" \
      -H "Content-Type: application/json" \
      -d "$body")
  else
    response=$(curl -s -w "\n%{http_code}" -X "$method" "$BASE_URL$path")
  fi

  status_code=$(printf "%s" "$response" | tail -n 1)
  response_body=$(printf "%s" "$response" | sed '$d')
}

check_status() {
  local expected=$1
  local label=$2

  if [ "$status_code" != "$expected" ]; then
    echo "FAILED: $label expected $expected but got $status_code"
    echo "$response_body"
    exit 1
  fi

  echo "PASSED: $label"
}

json_value() {
  local field=$1
  printf "%s" "$response_body" | sed -n "s/.*\"$field\":\"\\([^\"]*\\)\".*/\\1/p" | head -n 1
}

timestamp=$(date +%s)

echo "Running API Gateway tests against $BASE_URL"

call_api DELETE "/tickets/DOES-NOT-EXIST-$timestamp"
if [ "$status_code" = "204" ] || [ "$status_code" = "404" ]; then
  echo "PASSED: initial DELETE reached API Gateway"
else
  echo "FAILED: initial DELETE expected 204 or 404 but got $status_code"
  echo "$response_body"
  exit 1
fi

asset_request='{"type":"LAPTOP","status":"IN_SERVICE"}'
call_api POST "/assets" "$asset_request"
check_status 201 "POST asset through gateway"
asset_id=$(json_value "assetId")

call_api GET "/assets/$asset_id"
check_status 200 "GET asset by id through gateway"

customer_request="{\"firstName\":\"Gateway$timestamp\",\"lastName\":\"Tester\",\"email\":\"gateway.$timestamp@test.com\",\"department\":\"IT\"}"
call_api POST "/customers" "$customer_request"
check_status 201 "POST customer through gateway"
customer_id=$(json_value "customerId")

call_api GET "/customers/$customer_id"
check_status 200 "GET customer by id through gateway"

staff_request="{\"fistName\":\"Gateway$timestamp\",\"lastName\":\"Agent\",\"email\":\"gateway.agent.$timestamp@test.com\",\"staffRole\":\"AGENT\"}"
call_api POST "/staff" "$staff_request"
check_status 201 "POST staff through gateway"
staff_id=$(json_value "staffId")

call_api GET "/staff/$staff_id"
check_status 200 "GET staff by id through gateway"

ticket_request="{\"title\":\"Gateway test ticket\",\"description\":\"Testing ticket endpoints through the API Gateway\",\"customerId\":\"$customer_id\",\"staffId\":\"$staff_id\",\"assetId\":\"$asset_id\",\"status\":\"NEW\",\"priority\":\"HIGH\"}"
call_api POST "/tickets" "$ticket_request"
check_status 201 "POST ticket through gateway"
ticket_id=$(json_value "ticketId")

call_api GET "/tickets"
check_status 200 "GET all tickets through gateway"

call_api GET "/tickets/$ticket_id"
check_status 200 "GET ticket by id through gateway"

ticket_update="{\"title\":\"Gateway updated ticket\",\"description\":\"Testing ticket update through the API Gateway\",\"customerId\":\"$customer_id\",\"staffId\":\"$staff_id\",\"assetId\":\"$asset_id\",\"status\":\"IN_PROGRESS\",\"priority\":\"MEDIUM\"}"
call_api PUT "/tickets/$ticket_id" "$ticket_update"
check_status 200 "PUT ticket through gateway"

call_api DELETE "/tickets/$ticket_id"
check_status 204 "DELETE ticket through gateway"

call_api DELETE "/assets/$asset_id"
check_status 204 "Cleanup asset through gateway"

call_api DELETE "/customers/$customer_id"
check_status 204 "Cleanup customer through gateway"

call_api DELETE "/staff/$staff_id"
check_status 204 "Cleanup staff through gateway"

echo "All API Gateway bash tests passed."
