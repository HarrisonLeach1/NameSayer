# Namesayer_part3
The next part of the Namesayer project. A Java GUI that simulates the authoring component of a voice-activated name pronouncing aid. Has list, play, create, and delete operations.

## Purpose
1. To create the practise module for Namesayer for users to say unfamiliar names.  
2. To gain experience using media playing tools (eg ffmpeg).
3. To gain experience working in small groups.
4. To gain experience in giving presentations.

## Interface Requirements

### Names List requirements
1. The user should be able to select what names, from the database they wish to practise.
   1. They should be able to select whether they want to practise: a single name, or a list of names 
2. If a list of names is selected, users should be given the choice to have the list order randomised.

### Pronunciation Practise
3. Users should be able to listen  recordings of the selected namesa.If  there  are  multiple  version  of  a  name,  the  application  needs  to  handle  this  sensibly.
4. Users should be able to listen to an unfamiliar name, then make their own recording, and  then  be  able  to  listen  and  compare  their  recording  with  that  in  the  names  database.
   1. Users should have a choice to save their own productions of a name.
   2. Users  should  be  able  to  access  all  saved  past  attempts  of  names.  These  audio files must be sensibly named.
5. If a recording in the names database is of bad quality, users should  be able to give it a  bad  quality  rating,  and  have  that  file  information  stored  in  a  text  file  on  their  machine.
6. There  should  be  a  function  on  the  app  that  enables  users  to  test  the  level  of  their  microphone, to make sure their microphone is working.

### Ease of Use
7. This interface has to be easy to use for a non technical person:
   1. This includes the look and feel of the interface.
   2. The read-ability of the interface.
   3. The robustness of the interface. 
   4. The interface should not freeze. 
