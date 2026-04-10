#!/bin/sh
set -eu

NACOS_ADDR="${NACOS_ADDR:-http://nacos:8848}"
NAMESPACE_ID="${NAMESPACE_ID:-prod}"
BASE_DIR="${BASE_DIR:-/nacos-config}"

echo "等待 Nacos 就绪..."

until curl -fsS "${NACOS_ADDR}/nacos/" >/dev/null 2>&1; do
  sleep 2
done

echo "Nacos 已就绪，开始导入配置..."

for group_dir in "$BASE_DIR"/*; do
  [ -d "$group_dir" ] || continue

  group=$(basename "$group_dir")

  for file in "$group_dir"/*.yaml; do
    [ -f "$file" ] || continue

    dataId=$(basename "$file")

    echo "导入中: group=$group, dataId=$dataId"

    curl -fsS -X POST "${NACOS_ADDR}/nacos/v2/cs/config" \
      -d "namespaceId=${NAMESPACE_ID}" \
      -d "group=${group}" \
      -d "dataId=${dataId}" \
      --data-urlencode "content@${file}" \
      -d "type=yaml" >/dev/null

    echo "导入完成: group=$group, dataId=$dataId"
  done
done

echo "全部导入完成"