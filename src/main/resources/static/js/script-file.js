let arrImages=[];
let fileInput=document.getElementById("file-input");
let filearr=document.getElementById("filearr");
let imageContainer=document.getElementById("images");
let numOfFiles=document.getElementById("num-of-files");
function preview(){
	for(i of fileInput.files){
		if(arrImages.every(e => e.name !== i.name)) arrImages.push(i);
	}
	fileInput.value=null;
	show();
}

function deleteItem(index){
	arrImages.splice(index, 1);
	show();
}

function show(){
	imageContainer.innerHTML="";
	numOfFiles.textContent=arrImages.length+' ảnh được chọn';	
	for(let i=0;i<arrImages.length;i++){
		let reader=new FileReader();
		let figure=document.createElement("figure");
		let figCap=document.createElement("figcaption");
		figCap.innerText=arrImages[i].name;
		figure.appendChild(figCap);
		reader.onload=()=>{
			let img=document.createElement("img");
			let span=document.createElement("span");
			span.innerHTML="&times";
			span.setAttribute("onclick","deleteItem("+i+")");
			img.setAttribute("src",reader.result);
			figure.insertBefore(img, figCap);
			figure.insertBefore(span, img);
		}
		imageContainer.appendChild(figure);
		reader.readAsDataURL(arrImages[i]);
	}
	
}

