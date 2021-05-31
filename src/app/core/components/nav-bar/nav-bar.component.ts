import { Component, OnInit } from '@angular/core';
import { LoginService } from 'src/app/shared/services/login.service';

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.css']
})
export class NavBarComponent implements OnInit {

  private mostraMenu = false;
  constructor(
    private loginService: LoginService
  ) { }

  ngOnInit() {
    
  }

  ngAfterContentChecked(){
    this.mostraMenu = this.loginService.isLoggedIn();
  }

}
