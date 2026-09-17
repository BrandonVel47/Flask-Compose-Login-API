from flask import Flask, jsonify, request
from flask_sqlalchemy import SQLAlchemy
from flask_bcrypt import Bcrypt
import os

from flask_jwt_extended import JWTManager, create_access_token, jwt_required, get_jwt_identity

app = Flask(__name__)

# 1. Configuración de la Base de Datos (SQLite)
# El archivo se guardará en la carpeta del contenedor como 'site.db'
app.config['SQLALCHEMY_DATABASE_URI'] = os.environ.get('DATABASE_URL')
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

app.config['JWT_SECRET_KEY'] = os.environ.get('JWT_SECRET_KEY', 'clave-super-secreta-de-brandon-escom')
jwt = JWTManager(app)

db = SQLAlchemy(app)
bcrypt = Bcrypt(app)

# 2. Modelo de Usuario (La tabla en la BD)
class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(20), unique=True, nullable=False)
    password = db.Column(db.String(60), nullable=False) # Aquí guardaremos el hash

    def __repr__(self):
        return f"User('{self.username}')"

# Modelo para el CRUD de Vehículos
class Vehiculo(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    marca = db.Column(db.String(50), nullable=False)
    modelo = db.Column(db.String(50), nullable=False)
    estado = db.Column(db.String(50), nullable=False, default="Activo")

    def __repr__(self):
        return f"Vehiculo('{self.marca}', '{self.modelo}')"

# Modelo de Tarea para las operaciones CRUD
class Tarea(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    titulo = db.Column(db.String(100), nullable=False)
    descripcion = db.Column(db.String(255), nullable=True)

    def to_dict(self):
        return {
            "id": self.id,
            "titulo": self.titulo,
            "descripcion": self.descripcion
        }

# 3. Rutas

@app.route('/')
def hello():
    return jsonify({"message": "API Funcionando"})

# Endpoint de REGISTRO
@app.route('/register', methods=['POST'])
def register():
    data = request.get_json()
    username = data.get('username')
    password = data.get('password')

    # Verificar si el usuario ya existe
    if User.query.filter_by(username=username).first():
        return jsonify({"message": "El usuario ya existe"}), 400

    # Encriptar contraseña
    hashed_password = bcrypt.generate_password_hash(password).decode('utf-8')
    
    # Crear y guardar nuevo usuario
    new_user = User(username=username, password=hashed_password)
    db.session.add(new_user)
    db.session.commit()

    return jsonify({"message": "Usuario creado exitosamente"}), 201

# Endpoint de LOGIN
@app.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    username = data.get('username')
    password = data.get('password')

    user = User.query.filter_by(username=username).first()

    # Verificamos si el usuario existe y si la contraseña coincide con el hash
    if user and bcrypt.check_password_hash(user.password, password):
        
        # 1. Creamos el token usando el ID del usuario
        access_token = create_access_token(identity=str(user.id))
        
        # 2. Se lo enviamos al cliente en la respuesta
        return jsonify({
            "status": "success",
            "message": "Login exitoso",
            "user_id": user.id,
            "username": user.username,
            "access_token": access_token 
        }), 200
    else:
        return jsonify({"status": "error", "message": "Credenciales inválidas"}), 401

# ==========================================
# RUTAS CRUD (VEHICULOS)
# ==========================================

# 1. CREATE
@app.route('/vehiculos', methods=['POST'])
@jwt_required()
def create_vehiculo():
    data = request.get_json()
    nuevo_vehiculo = Vehiculo(
        marca=data.get('marca'), 
        modelo=data.get('modelo'),
        estado=data.get('estado', 'Activo')
    )
    db.session.add(nuevo_vehiculo)
    db.session.commit()
    return jsonify({"mensaje": "Vehículo registrado exitosamente"}), 201

# 2. READ
@app.route('/vehiculos', methods=['GET'])
@jwt_required()
def get_vehiculos():
    vehiculos = Vehiculo.query.all()
    resultado = [
        {"id": v.id, "marca": v.marca, "modelo": v.modelo, "estado": v.estado} 
        for v in vehiculos
    ]
    return jsonify(resultado), 200

# 3. UPDATE
@app.route('/vehiculos/<int:id>', methods=['PUT'])
@jwt_required()
def update_vehiculo(id):
    vehiculo = Vehiculo.query.get(id)
    if not vehiculo:
        return jsonify({"error": "Vehículo no encontrado"}), 404
    
    data = request.get_json()
    vehiculo.marca = data.get('marca', vehiculo.marca)
    vehiculo.modelo = data.get('modelo', vehiculo.modelo)
    vehiculo.estado = data.get('estado', vehiculo.estado)
    
    db.session.commit()
    return jsonify({"mensaje": "Vehículo actualizado exitosamente"}), 200

# 4. DELETE
@app.route('/vehiculos/<int:id>', methods=['DELETE'])
@jwt_required()
def delete_vehiculo(id):
    vehiculo = Vehiculo.query.get(id)
    if not vehiculo:
        return jsonify({"error": "Vehículo no encontrado"}), 404
    
    db.session.delete(vehiculo)
    db.session.commit()
    return jsonify({"mensaje": "Vehículo eliminado exitosamente"}), 200

if __name__ == '__main__':
    # Esto crea las tablas automáticamente si no existen al iniciar
    with app.app_context():
        db.create_all()
    
    app.run(host='0.0.0.0', port=5000, debug=True)
