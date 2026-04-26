#!/bin/bash
# =============================================================
# SCRIPT 2b - TERMINAL 2: Spark Worker 1
# Práctica 1 - Análisis de Tráfico Aéreo
#
# IMPORTANTE: Ejecutar DESPUÉS de que spark-master esté activo
# =============================================================

SPARK_IMAGE="mafernandezd/spark:3.5.7-scala2.12-java17-r-ubuntu"
NETWORK="traffic-net"

echo ""
echo "============================================================="
echo "  PASO 1: Arrancar contenedor Spark Worker 1 (background)"
echo "  Cores: 3 | Memoria: 2GB"
echo "  Master: spark://spark-master:7077"
echo "============================================================="
echo ""
CMD="docker run -dit \
  --name spark-worker1 \
  --network $NETWORK \
  $SPARK_IMAGE"
echo ">>> $CMD"
eval "$CMD"
echo "    ✔ Contenedor 'spark-worker1' arrancado."

echo ""
echo "============================================================="
echo "  PASO 2: Activar Spark Worker 1"
echo "============================================================="
echo ""
echo ">>> docker exec spark-worker1 sbin/start-worker.sh spark://spark-master:7077 -c 3 -m 2GB"
docker exec spark-worker1 sbin/start-worker.sh spark://spark-master:7077 -c 3 -m 2GB
echo "    ✔ Spark Worker 1 activo."

echo ""
echo "============================================================="
echo "  ✅  Worker 1 listo."
echo "============================================================="
echo ""
