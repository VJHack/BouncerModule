# Empty file for now, but will contain real code later this week.

# Will take an image and pull the face out of it, then return the face as a numpy array (probably)
def face_recognition(img):
    pass

# Will take two faces and compare them to see if they are the same person (if we want this feature)
def face_matching(face1, face2):
    pass

def main():
    # will get the images from the camera, or be passed in from app
    person_image = None
    id_image = None
    # will get the faces from the images
    person_face = face_recognition(person_image)
    id_face = face_recognition(id_image)
    # will compare the faces to see if they are the same person
    if face_matching(person_face, id_face):
        print("Match!")
        