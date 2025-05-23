#!/bin/bash
# PoblarCentro.sh
# Script para poblar el sistema CentroEducativo con datos iniciales

# 1. Login como administrador para obtener sesión
echo "Autenticando administrador..."
curl -s --data '{"dni":"111111111","password":"654321"}' \
  -X POST -H "content-type: application/json" \
  http://localhost:9090/CentroEducativo/login -c admin_cookie.txt -b admin_cookie.txt > key.txt

KEY=$(cat key.txt)
echo "Clave de sesión obtenida: $KEY"

# Función para añadir alumno
add_alumno() {
  dni=$1
  nombre=$2
  apellidos=$3
  echo "Añadiendo alumno: $dni, $nombre $apellidos"
  curl -s --data "{\"dni\": \"$dni\", \"nombre\": \"$nombre\", \"apellidos\": \"$apellidos\", \"password\": \"123456\"}" \
    -X POST -H "content-type: application/json" \
    "http://localhost:9090/CentroEducativo/alumnos?key=$KEY" -c admin_cookie.txt -b admin_cookie.txt
}

# Función para añadir profesor
add_profesor() {
  dni=$1
  nombre=$2
  apellidos=$3
  echo "Añadiendo profesor: $dni, $nombre $apellidos"
  curl -s --data "{\"dni\": \"$dni\", \"nombre\": \"$nombre\", \"apellidos\": \"$apellidos\", \"password\": \"123456\"}" \
    -X POST -H "content-type: application/json" \
    "http://localhost:9090/CentroEducativo/profesores?key=$KEY" -c admin_cookie.txt -b admin_cookie.txt
}

# Función para añadir asignatura
add_asignatura() {
  acronimo=$1
  nombre=$2
  curso=$3
  cuatrimestre=$4
  creditos=$5
  echo "Añadiendo asignatura: $acronimo - $nombre"
  curl -s --data "{\"acronimo\": \"$acronimo\", \"nombre\": \"$nombre\", \"curso\": $curso, \"cuatrimestre\": \"$cuatrimestre\", \"creditos\": $creditos}" \
    -X POST -H "content-type: application/json" \
    "http://localhost:9090/CentroEducativo/asignaturas?key=$KEY" -c admin_cookie.txt -b admin_cookie.txt
}

# 2. Lista de alumnos (dni,nombre,apellidos)
alumnos=(
  "12416428W,Francisco,Rivas Sanchez"
  "23736742R,Antonio,Gonzalez Gomez"
  "93041234F,Unai,Belasco Llopis"
  "54769206M,Sergio,Benito Torres"
  "85239462W,Fernanda,Alonso Perez"
)

# Añadir alumnos
for alumno in "${alumnos[@]}"; do
  IFS=',' read -r dni nombre apellidos <<< "$alumno"
  add_alumno "$dni" "$nombre" "$apellidos"
done

# 3. Lista de profesores (dni,nombre,apellidos)
profesores=(
  "67341358H,Ramon,Martin"
  "19572863L,Pepe,Villegas"
  "06468732A,Manuel,Mon"
  "652423454M,Jose,Fons"
)

# Añadir profesores
for profesor in "${profesores[@]}"; do
  IFS=',' read -r dni nombre apellidos <<< "$profesor"
  add_profesor "$dni" "$nombre" "$apellidos"
done

# 4. Lista de asignaturas (acronimo,nombre,curso,cuatrimestre,creditos)
asignaturas=(
  "IPC,Interfaces Persona Computador,3,B,4.5"
  "ADS,Administración de Sistemas,4,A,4.5"
  "Tal,Teoría de Autómatas y lenguajes formales,4,A,4.5"
)

# Añadir asignaturas
for asignatura in "${asignaturas[@]}"; do
  IFS=',' read -r acronimo nombre curso cuatrimestre creditos <<< "$asignatura"
  add_asignatura "$acronimo" "$nombre" "$curso" "$cuatrimestre" "$creditos"
done

echo "Datos insertados correctamente en CentroEducativo."
