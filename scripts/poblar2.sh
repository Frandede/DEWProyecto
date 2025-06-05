#!/bin/bash
# PoblarCentro_Extendido.sh
# - No modifica alumnos/profesores/asignaturas existentes.
# - Crea nuevos alumnos y nuevos profesores.
# - Crea “IPC” si no existe.
# - Matricula a los nuevos alumnos (POST en /asignaturas/{acr}/alumnos).
# - Asigna notas a:
#     • Alumnos existentes: en sus 3 asignaturas (DEW, IAP, DCU).
#     • Alumnos nuevos: en las 4 asignaturas.
# - Cada profesor sólo asigna nota en las asignaturas que imparte.

########################################
# 1. LOGIN ADMINISTRADOR               #
########################################
echo " Autenticando administrador..."
ADMIN_KEY=$(curl -s --data '{"dni":"111111111","password":"654321"}' \
  -X POST -H "Content-Type: application/json" \
  http://localhost:9090/CentroEducativo/login \
  -c admin_cookie.txt -b admin_cookie.txt)

if [[ -z "$ADMIN_KEY" ]]; then
  echo "❌ Error: no se obtuvo clave del administrador. Verifica DNI/contraseña."
  exit 1
fi
echo "✅ Clave del administrador obtenida."
echo

########################################
# 2. CREAR ASIGNATURA IPC (si no existe)#
########################################
echo "➕ Intentando crear asignatura IPC..."
curl -s --data '{"acronimo":"IPC","nombre":"Interacción Persona–Computador","curso":3,"cuatrimestre":"B","creditos":4.5}' \
  -X POST -H "Content-Type: application/json" \
  "http://localhost:9090/CentroEducativo/asignaturas?key=$ADMIN_KEY" \
  -c admin_cookie.txt -b admin_cookie.txt \
  >/dev/null
echo "✅ (Si no existía, IPC ha sido creada.)"
echo

########################################
# 3. LISTADO DE ALUMNOS EXISTENTES     #
########################################
alumnos_existentes=(
  "12345678W"   # Pepe García Sánchez
  "23456387R"   # Maria Fernández Gómez
  "34567891F"   # Miguel Hernández Llopis
  "93847525G"   # Laura Benitez Torres
  "37264096W"   # Minerva Alonso Pérez
)

########################################
# 4. LISTADO DE PROFESORES EXISTENTES  #
########################################
profesores_existentes=(
  "23456733H"   # Ramón García
  "10293756L"   # Pedro Valderas
  "06374291A"   # Manoli Albert
  "65748923M"   # Joan Fons
)

########################################
# 5. CREAR NUEVOS PROFESORES           #
########################################
nuevos_profesores=(
  "78901234X,Ana,Lopez"
  "89012345Y,Luis,Martinez"
)

add_profesor() {
  local dni="$1"
  local nombre="$2"
  local apellidos="$3"
  echo "➕ Añadiendo profesor: $dni, $nombre $apellidos"
  curl -s --data "{\"dni\": \"$dni\", \"nombre\": \"$nombre\", \"apellidos\": \"$apellidos\", \"password\": \"123456\"}" \
    -X POST -H "Content-Type: application/json" \
    "http://localhost:9090/CentroEducativo/profesores?key=$ADMIN_KEY" \
    -c admin_cookie.txt -b admin_cookie.txt \
    >/dev/null
}

echo " Creando nuevos profesores..."
for prof in "${nuevos_profesores[@]}"; do
  IFS=',' read -r dni nombre apellidos <<< "$prof"
  add_profesor "$dni" "$nombre" "$apellidos"
done
echo "✅ Profesores nuevos creados."
echo

########################################
# 6. CREAR NUEVOS ALUMNOS              #
########################################
nuevos_alumnos=(
  "45678912Z,Lucia,Lopez Morales"
  "56789123T,Alberto,Gomez Ruiz"
  "67891234K,Sofia,Martinez Perez"
)

add_alumno() {
  local dni="$1"
  local nombre="$2"
  local apellidos="$3"
  echo "➕ Añadiendo alumno: $dni, $nombre $apellidos"
  curl -s --data "{\"dni\": \"$dni\", \"nombre\": \"$nombre\", \"apellidos\": \"$apellidos\", \"password\": \"123456\"}" \
    -X POST -H "Content-Type: application/json" \
    "http://localhost:9090/CentroEducativo/alumnos?key=$ADMIN_KEY" \
    -c admin_cookie.txt -b admin_cookie.txt \
    >/dev/null
}

echo " Creando nuevos alumnos..."
for alum in "${nuevos_alumnos[@]}"; do
  IFS=',' read -r dni nombre apellidos <<< "$alum"
  add_alumno "$dni" "$nombre" "$apellidos"
done
echo "✅ Alumnos nuevos creados."
echo

########################################
# 7. LISTADO COMPLETO DE ALUMNOS PARA NOTAS
########################################
alumnos_todos=("${alumnos_existentes[@]}")
for alum in "${nuevos_alumnos[@]}"; do
  IFS=',' read -r dni _ <<< "$alum"
  alumnos_todos+=("$dni")
done

########################################
# 8. LISTADO COMPLETO DE PROFESORES PARA NOTAS
########################################
declare -A profesores_clave=()
# Populate with existing professors
for prof_dni in "${profesores_existentes[@]}"; do
  profesores_clave["$prof_dni"]="123456" # Assuming a default password for existing
done
# Populate with new professors
for prof_full in "${nuevos_profesores[@]}"; do
  IFS=',' read -r dni _ <<< "$prof_full"
  profesores_clave["$dni"]="123456"
done


########################################
# 9. LISTA DE ASIGNATURAS              #
########################################
asignaturas_existentes=( "DEW" "IAP" "DCU" )
asignaturas_nuevas=( "IPC" )
asignaturas_todas=( "${asignaturas_existentes[@]}" "${asignaturas_nuevas[@]}" )

########################################
# 10. MATRÍCULA DE NUEVOS ALUMNOS      #
########################################
matricular_alumno() {
  local alumno_dni="$1"
  local acronimo="$2"

  echo " Matriculando alumno $alumno_dni en asignatura $acronimo..."
  curl -s -X POST "http://localhost:9090/CentroEducativo/asignaturas/$acronimo/alumnos?key=$ADMIN_KEY" \
    -H "Content-Type: application/json" \
    -b admin_cookie.txt -c admin_cookie.txt \
    -d "$alumno_dni" \
    >/dev/null
}

echo " Matriculando nuevos alumnos en DEW, IAP, DCU e IPC..."
for alum_full in "${nuevos_alumnos[@]}"; do
  IFS=',' read -r dni _ <<< "$alum_full"
  for asig in "${asignaturas_todas[@]}"; do
    matricular_alumno "$dni" "$asig"
  done
done
echo "✅ Matrículas de nuevos alumnos completadas."
echo


########################################
# 11. ASIGNACIÓN DE PROFESORES A ASIGNATURAS
########################################
# This mapping dictates which professors *can* teach which subject
declare -A profesor_para_asign
profesor_para_asign["DEW"]="23456733H 65748923M"
profesor_para_asign["IAP"]="23456733H 10293756L"
profesor_para_asign["DCU"]="10293756L 06374291A"
profesor_para_asign["IPC"]="06374291A 65748923M"

# Add new professors to existing assignments
profesor_para_asign["DCU"]+=" 78901234X" # Ana Lopez teaches DCU
profesor_para_asign["IPC"]+=" 78901234X" # Ana Lopez teaches IPC
profesor_para_asign["IAP"]+=" 89012345Y" # Luis Martinez teaches IAP
profesor_para_asign["IPC"]+=" 89012345Y" # Luis Martinez teaches IPC

# Function to assign a subject to a professor
assign_prof_to_subject() {
  local prof_dni="$1"
  local acronimo="$2"
  echo " Asignando asignatura $acronimo a profesor $prof_dni..."
  # CRITICAL CHANGE: Sending the acronym as a plain string, not a JSON string.
  curl -s -X POST "http://localhost:9090/CentroEducativo/profesores/$prof_dni/asignaturas?key=$ADMIN_KEY" \
    -H "Content-Type: application/json" \
    -b admin_cookie.txt -c admin_cookie.txt \
    -d "$acronimo" \
    >/dev/null
}

echo " Asignando asignaturas a profesores..."
# Assign existing professors to their subjects
for asig in "${!profesor_para_asign[@]}"; do
    profs=( ${profesor_para_asign[$asig]} )
    for prof_dni in "${profs[@]}"; do
        # Only assign if the professor is one of the initially defined ones
        # or new professors (already created)
        if [[ " ${profesores_existentes[@]} " =~ " ${prof_dni} " || "$(echo "${nuevos_profesores[@]}" | grep "$prof_dni")" ]]; then
            assign_prof_to_subject "$prof_dni" "$asig"
        fi
    done
done
echo "✅ Asignación de asignaturas a profesores completada."
echo


########################################
# 12. OBTENER CLAVE DE PROFESOR        #
########################################
get_prof_key() {
  local prof_dni="$1"
  local prof_pass="${profesores_clave[$prof_dni]}"
  if [[ -z "$prof_pass" ]]; then
    echo ""
    return
  fi
  local resp
  resp=$(curl -s --data "{\"dni\":\"$prof_dni\",\"password\":\"$prof_pass\"}" \
    -X POST -H "Content-Type: application/json" \
    http://localhost:9090/CentroEducativo/login)
  echo "$resp"
}

########################################
# 13. ASIGNAR NOTA (POR PROFESOR)      #
########################################
asignar_nota() {
  local alumno_dni="$1"
  local acronimo="$2"
  local nota="$3"
  local prof_dni="$4"

  local prof_key
  prof_key=$(get_prof_key "$prof_dni")
  if [[ -z "$prof_key" ]]; then
    echo "❌ Error: no se pudo loguear profesor $prof_dni. Se omite nota."
    return
  fi

  echo " Profesor $prof_dni asigna nota $nota a alumno $alumno_dni en $acronimo"
  curl -s -X PUT \
    "http://localhost:9090/CentroEducativo/alumnos/$alumno_dni/asignaturas/$acronimo?key=$prof_key" \
    -H "Content-Type: application/json" \
    --data "$nota" \
    >/dev/null
}

########################################
# 14. GENERAR Y ASIGNAR NOTAS          #
########################################
echo " Asignando notas de ejemplo..."

for alumno in "${alumnos_todos[@]}"; do
  if printf '%s\n' "${alumnos_existentes[@]}" | grep -qx "$alumno"; then
    cursos_alumno=( "DEW" "IAP" "DCU" )
  else
    cursos_alumno=( "DEW" "IAP" "DCU" "IPC" )
  fi

  idx_alumno=-1
  for i in "${!alumnos_todos[@]}"; do
    [[ "${alumnos_todos[$i]}" == "$alumno" ]] && idx_alumno="$i"
  done

  for asig in "${cursos_alumno[@]}"; do
    nota=$(awk -v min=5 -v max=10 'BEGIN{srand(); printf "%.1f", min+rand()*(max-min)}')
    profs=( ${profesor_para_asign[$asig]} )
    prof_dni="${profs[$(( idx_alumno % ${#profs[@]} ))]}"
    asignar_nota "$alumno" "$asig" "$nota" "$prof_dni"
  done
done

echo "✅ Notas asignadas a todos los alumnos (existentes y nuevos)."
echo

echo "🎉 ¡Proceso finalizado! Cada alumno tiene sus notas y los nuevos profesores han impartido sus asignaturas."

