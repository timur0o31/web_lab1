document.getElementById('form1').addEventListener('submit', function(event) {
				    event.preventDefault(); 
				    
				    const xCheckedElement = document.querySelector('input[name="number"]:checked');
				    const xValue = xCheckedElement ? xCheckedElement.value : null; 
				    const yValue = document.getElementById('y-value').value; 
				    const rValue = document.getElementById('R-value').value;
				    const currentTime = new Date().toLocaleString(); 

				    if (!validateArgument(xValue, yValue,rValue)) return; 

				    const data = {
				        x: xValue, 
				        y: yValue, 
				        r: rValue
				    };
				    //alert(data.x + " " + data.y + " " + data.r);

				    fetch("./fcgi-bin/webLab1.jar", {
				        method: 'POST',
				         headers: {
				            "Content-Type": "application/json",
				        },
				        body: JSON.stringify(data)
				    })
				    .then(response => {
				        if (!response.ok) 
				            throw new Error('network or server error');
				        return response.json();
				    })
				    .then(response => {
				        const hitResult = response.result === "true" ? "hit" : "miss";
				        const executionTime = response.time;
				        addToTableRow(xValue, yValue, rValue, currentTime, executionTime, hitResult);
				    })
				    .catch(error => {
   							 console.error('error:', error);
   							});

					});
function addToTableRow(xValue, yValue,rValue, currentTime, executionTime, hitResult){
						const table = document.getElementById('table_results');
				           
				            const newRow = document.createElement('tr');

				            const xCell = document.createElement('td');
				            xCell.textContent = xValue;
				            newRow.appendChild(xCell);

				            const yCell = document.createElement('td');
				            yCell.textContent = Number(yValue);
				            newRow.appendChild(yCell);

				            const rCell = document.createElement('td');
				            rCell.textContent = rValue;
				            newRow.appendChild(rCell);

				            const hitResultCell = document.createElement('td');
				            if (hitResult === "hit") {
        							hitResultCell.style.color = 'green'; 
    						} else {
        							hitResultCell.style.color = 'red'; 
    						}
    						hitResultCell.textContent = hitResult;
    						newRow.appendChild(hitResultCell);

				            const executionTimeCell = document.createElement('td');
				            executionTimeCell.textContent = executionTime;
				            newRow.appendChild(executionTimeCell);

				            const currentTimeCell = document.createElement('td');
				            currentTimeCell.textContent = currentTime;
				            newRow.appendChild(currentTimeCell);
		
						    const firstDataRow = table.querySelector('tr:nth-child(2)');
						    if (firstDataRow) {
						        table.insertBefore(newRow, firstDataRow);
						    } else {
						        table.appendChild(newRow);
						    }
        			}
function validateArgument(xValue, yValue, rValue){
        				const x = parseInt(xValue);
        				const y = parseFloat(yValue);
        				const r = parseInt(rValue);
        				document.getElementById('error-message-x').innerHTML = "";
    					document.getElementById('error-message-y').innerHTML = "";
    					document.getElementById('error-message-r').innerHTML = "";
        				if(xValue === null){
        					document.getElementById('error-message-x').innerHTML = "Значение для X должно быть выбрано!";
        					return false;
        				}
        				if(isNaN(y) || yValue.trim()==="" || /[^0-9.-]/.test(yValue)){
        					document.getElementById('error-message-y').innerHTML = "Y должен быть числом!";
        					return false;
        				}else if(y < -3 || y > 5){
        					document.getElementById('error-message-y').innerHTML = "Y должен лежать в пределах [-3; 5]!";
        					return false;
        				}
        				if(isNaN(r)){
        					document.getElementById('error-message-r').innerHTML = "Выберите R из списка";
        					return false;
        				}
        				document.getElementById('error-message-x').innerHTML = "";
    					document.getElementById('error-message-y').innerHTML = "";
    					document.getElementById('error-message-r').innerHTML = "";	
        				return true;
        			}