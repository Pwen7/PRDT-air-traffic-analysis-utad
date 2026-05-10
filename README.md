# Project: Air traffic Data Analysis

## ⚠️ Known issues
Windows users may encounter issues with line endings when running the scripts. To resolve this, you can use the following command to convert the line endings to Unix format:
```
PRDT-air-traffic-analysis-utad/scripts$ sed -i 's/\r$//' *
```

## 📁 Folder structure
```
└── 📁PRDT-analisis-trafico-aereo
    └── 📁data
        ├── datos_practica.tar.gz
    └── 📁scripts
        ├── 01_setup_infra.sh
        ├── 02a_spark_master.sh
        ├── 02b_spark_worker1.sh
        ├── 02c_spark_worker2.sh
        ├── 03_spark_shell.sh
    └── 📁src
        ├── 01_countries.sc
        ├── 02_airlines.sc
        ├── 03_airports.sc
        ├── 04_routes.sc
        ├── 05a_queries.sc
        ├── 05b_aggregations.sc
    └── README.md
```
