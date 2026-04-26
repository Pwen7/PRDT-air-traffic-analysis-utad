#!/bin/bash
# =============================================================
# SCRIPT 2a - TERMINAL 1: Spark Master
# Práctica 1 - Análisis de Tráfico Aéreo
# =============================================================

SPARK_IMAGE="mafernandezd/spark:3.5.7-scala2.12-java17-r-ubuntu"
NETWORK="traffic-net"

echo ""
echo "============================================================="
echo "  LIMPIEZA PREVIA: Eliminando contenedores Spark existentes"
echo "============================================================="

SPARK_CONTAINERS=("spark-master" "spark-worker1" "spark-worker2" "spark-app")
for c in "${SPARK_CONTAINERS[@]}"; do
  echo ""
  echo ">>> docker rm -f $c"
  docker rm -f "$c" 2>/dev/null && echo "    ✔ Contenedor '$c' eliminado." || echo "    ℹ  Contenedor '$c' no existía, se omite."
done

echo ""
echo "============================================================="
echo "  PASO 1: Arrancar contenedor Spark Master (background)"
echo "  Puerto: 8080 (WebUI Spark)"
echo "============================================================="
echo ""
CMD="docker run -dit \
  --name spark-master \
  --network $NETWORK \
  -p 8080:8080 \
  $SPARK_IMAGE"
echo ">>> $CMD"
eval "$CMD"
echo "    ✔ Contenedor 'spark-master' arrancado."

echo ""
echo "============================================================="
echo "  PASO 2: Activar Spark Master Node"
echo "============================================================="
echo ""
echo ">>> docker exec spark-master sbin/start-master.sh"
docker exec spark-master sbin/start-master.sh
echo "    ✔ Spark Master activo."
echo "    → WebUI Spark: http://localhost:8080"

echo ""
echo "============================================================="
echo "  ✅  Master listo. Ahora ejecuta en otras dos terminales:"
echo "      ./02b_spark_worker1.sh"
echo "      ./02c_spark_worker2.sh"
echo "============================================================="
echo ""
