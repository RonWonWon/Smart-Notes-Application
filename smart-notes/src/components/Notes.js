import React, { useEffect, useState } from 'react';
import { makeStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import { Container, Paper, Button } from '@material-ui/core';
import Dialog from '@material-ui/core/Dialog'
import DialogTitle from '@material-ui/core/DialogTitle';
import Chip from '@mui/joy/Chip';
import ChipDelete from '@mui/joy/ChipDelete';


const useStyles = makeStyles((theme) => ({
    root: {
        '& > *': {
            margin: theme.spacing(1),

        },
    },
}));

export default function Note() {
    const paperStyle = { padding: '50px 20px', width: 600, margin: "20px auto" }
    const [title, setTitle] = useState("")
    const [notes, setNotes] = useState("")
    const [allnotes, setAllNotes] = useState([])
    const [refresh, setRefresh] = useState(true);
    const classes = useStyles();
    const [open, setOpen] = useState(false);
    const [mailId, setMailId] = useState(""); 
    const [curId, setCurId] = useState(0); 
    const [update, setUpdate] = useState(false);


    function handleAdd(e) {
        e.preventDefault()
        const note = { title, notes }
        console.log(note)
        if(update){
            fetch("http://localhost:8080/note/update/"+curId, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(note)

            }).then(() => {
                console.log("Note Updated")
                setUpdate(false);
                setRefresh(true);
            })
        }
        else{
            fetch("http://localhost:8080/note/add", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(note)

            }).then(() => {
                console.log("New Note added")
                setRefresh(true);
            })
        }
    }

    function handleDownload(id) {
        console.log(id);
        var link = "http://localhost:8080/note/pdf/"+id;
        console.log(link);
        window.open(link);
    }

    function sendMail(){
        fetch("http://localhost:8080/note/share/mail/"+curId+"/"+mailId, {
            method: "GET",
            headers: { "Content-Type": "application/json" }

        }).then(() => {
            console.log("Mail Sent")
            alert("Mail Sent")
            setOpen(false)
        })
    }

    function deleteNote(id){
        fetch("http://localhost:8080/note/delete/"+id, {
            method: "DELETE"
        })
        setRefresh(true);
    }

    function editNote(note){
        window.scrollTo(0,0);
        setTitle(note.title);
        setNotes(note.notes);
        setCurId(note.id);
        setUpdate(true);
    }

    useEffect(() => {
        if(refresh){
            fetch("http://localhost:8080/note/show")
                .then(res => res.json())
                .then((result) => {
                    setAllNotes(result);
                }
            )
            setRefresh(false);
        }
    }, [refresh])
    return (

        <Container>
            <Paper elevation={3} style={paperStyle}>
                <h1 style={{ color: "blue" }}><u>Add Note</u></h1>

                <form classtitle={classes.root} noValidate autoComplete="off">

                    <TextField id="outlined-basic" label="Title" variant="outlined" fullWidth
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                    />
                    <TextField id="outlined-basic" label="Type Here" variant="outlined" fullWidth multiline rows = {5} maxRows = {Infinity}
                        value={notes}
                        onChange={(e) => setNotes(e.target.value)}
                    />
                    <label>
                    <Button variant="contained" color="secondary" onClick={handleAdd}>
                        Submit
                    </Button>
                    </label>
                </form>

            </Paper>
            <br/>
            <h1 style={{ color: "blue" }}><u>Notes</u></h1>
            
            <Paper elevation={3} style={paperStyle}>
                {allnotes.slice().reverse().map(note => (
                    <Paper elevation={6} style={{ margin: "10px", padding: "15px", textAlign: "left" }} key={note.id}>
                        <p align="right">
                            <Chip
                                variant="soft"
                                color="danger"
                                endDecorator={<ChipDelete variant="plain" />} 
                                onClick={()=>{deleteNote(note.id)}}
                            >
                                Delete
                            </Chip> 
                        </p>
                        <center><h3><u>{note.title}</u></h3></center>
                        <center>{note.notes}</center>
                        <br></br>
                        <center>
                        <Button variant="contained" color="secondary" onClick={()=>{editNote(note)}}>
                        Edit
                        </Button>
                        <Button variant="contained" color="secondary" onClick={()=>{handleDownload(note.id)}}>
                        Download
                        </Button><Button variant="contained" color="secondary" onClick={()=>{setCurId(note.id); setOpen(true)}}>
                        Mail
                        </Button>
                        </center>
                    </Paper>
                ))
                }

            </Paper>

            <Dialog open={open} onClose={()=>{setOpen(false)}}>
                <DialogTitle>Share</DialogTitle>
                <TextField id="outlined-basic" label="Mail ID" variant="outlined" fullWidth
                    value={mailId}
                    onChange={(e) => setMailId(e.target.value)}
                />
                <Button variant="contained" color="secondary" onClick={() => { sendMail() }}>
                    Send
                </Button>
            </Dialog>

        </Container>
    );
}