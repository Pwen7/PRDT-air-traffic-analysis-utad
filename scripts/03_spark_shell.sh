#!/bin/bash
# =============================================================
# SCRIPT 3: Arrancar Spark Shell (contenedor spark-app)
# Práctica 1 - Análisis de Tráfico Aéreo
#
# IMPORTANTE: Ejecutar este script DESPUÉS de que el clúster
# Spark esté levantado (spark-master + workers activos).
# =============================================================

SPARK_IMAGE="mafernandezd/spark:3.5.7-scala2.12-java17-r-ubuntu"
NETWORK="traffic-net"


echo ""
echo "============================================================="
echo "  PASO 1: Arrancar contenedor spark-app (Driver)"
echo "  Puertos: 4040 (Spark Web UI) | 18080 (History Server)"
echo "============================================================="
echo ""
CMD="docker run -it \
  --name spark-app \
  --network $NETWORK \
  -p 4040:4040 \
  -p 18080:18080 \
  $SPARK_IMAGE"
echo ">>> $CMD"
echo ""
echo "    ℹ  A continuación se abrirá el contenedor de forma interactiva."
echo "    Una vez dentro, ejecuta el comando que aparece abajo."
echo ""
echo "============================================================="
echo "  PASO A EJECUTAR DENTRO DEL CONTENEDOR:"
echo ""
echo "  Lanzar Spark Shell:"
echo "    bin/spark-shell \\"
echo "      --master spark://spark-master:7077 \\"
echo "      --driver-memory 1G \\"
echo "      --executor-memory 2G \\"
echo "      --total-executor-cores 6 \\"
echo "      --executor-cores 3 \\"
echo "      --jars work-dir/postgresql-42.3.7.jar \\"
echo "      --packages org.mongodb.spark:mongo-spark-connector_2.12:10.3.0"
echo ""
echo "  → WebUI Spark Application: http://localhost:4040"
echo "============================================================="
echo ""
echo ">>> Iniciando contenedor spark-app... (modo interactivo)"
echo ""

eval "$CMD"
