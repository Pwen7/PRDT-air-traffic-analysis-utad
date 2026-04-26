#!/bin/bash
# =============================================================
# SCRIPT 1: Infraestructura base (Red + HDFS + Postgres + MongoDB)
# Práctica 1 - Análisis de Tráfico Aéreo
# =============================================================



echo ""
echo "============================================================="
echo "  LIMPIEZA PREVIA: Eliminando contenedores existentes"
echo "============================================================="

CONTAINERS=("hdfs-traffic" "postgres-traffic" "mongo-traffic")

for c in "${CONTAINERS[@]}"; do
  echo ""
  echo ">>> docker rm -f $c"
  docker rm -f "$c" 2>/dev/null && echo "    ✔ Contenedor '$c' eliminado." || echo "    ℹ  Contenedor '$c' no existía, se omite."
done

echo ""
echo "============================================================="
echo "  PASO 1: Eliminar y recrear la red 'traffic-net'"
echo "============================================================="
echo ""
echo ">>> docker network rm traffic-net"
docker network rm traffic-net 2>/dev/null && echo "    ✔ Red 'traffic-net' eliminada." || echo "    ℹ  La red 'traffic-net' no existía, se omite."
echo ""
echo ">>> docker network create traffic-net"
docker network create traffic-net
echo "    ✔ Red 'traffic-net' creada."

echo ""
echo "============================================================="
echo "  PASO 2: Arrancar contenedor HDFS (NameNode)"
echo "  Imagen: mafernandezd/hdfs:2.7.2"
echo "  Puertos: 8020 (HDFS) | 9870 (WebUI)"
echo "============================================================="
echo ""
CMD="docker run -dit --name hdfs-traffic \
  --network traffic-net \
  -p 8020:8020 \
  -p 9870:50070 \
  mafernandezd/hdfs:2.7.2"
echo ">>> $CMD"
eval "$CMD"
echo "    ✔ Contenedor 'hdfs-traffic' arrancado."
echo "    → WebUI disponible en: http://localhost:9870"
echo "    → (Esperar unos segundos si no responde de inmediato)"

echo ""
echo "============================================================="
echo "  PASO 3: Arrancar contenedor PostgreSQL"
echo "  Imagen: postgres"
echo "  Puertos: 5432"
echo "  Usuario: postgres | Password: postgres | DB: practica"
echo "============================================================="
echo ""
CMD="docker run -dit --name postgres-traffic \
  --network traffic-net \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=practica \
  -p 5432:5432 \
  postgres"
echo ">>> $CMD"
eval "$CMD"
echo "    ✔ Contenedor 'postgres-traffic' arrancado."

echo ""
echo "============================================================="
echo "  PASO 4: Arrancar contenedor MongoDB"
echo "  Imagen: mongo:latest"
echo "  Puerto: 27017"
echo "============================================================="
echo ""
CMD="docker run -dit --name mongo-traffic \
  --network traffic-net \
  -p 27017:27017 \
  mongo:latest"
echo ">>> $CMD"
eval "$CMD"
echo "    ✔ Contenedor 'mongo-traffic' arrancado."

echo ""
echo "============================================================="
echo "  VERIFICACIÓN: Estado de los contenedores"
echo "============================================================="
echo ""
echo ">>> docker ps --filter name=hdfs-traffic --filter name=postgres-traffic --filter name=mongo-traffic"
docker ps --filter name=hdfs-traffic --filter name=postgres-traffic --filter name=mongo-traffic

echo ""
echo "============================================================="
echo "  ✅  Script 1 completado."
echo "  Siguiente paso: Ejecutar 02a_spark_master.sh para el clúster Spark"
echo "============================================================="
echo ""
