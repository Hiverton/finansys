import { Component, Injector } from '@angular/core';
import { Validators } from '@angular/forms';

import { Category } from '../shared/category.model';
import { CategoryService } from '../shared/category.service';

import { BaseResourceFormComponent } from 'src/app/shared/components/base-resource-form/base-resource-form.component';

@Component({
  selector: 'app-category-form',
  templateUrl: './category-form.component.html',
  styleUrls: ['./category-form.component.css']
})
export class CategoryFormComponent extends BaseResourceFormComponent<Category> {

  constructor(private categoryService: CategoryService, protected injector: Injector) { 
    super(injector, new Category(), categoryService, Category.fromJson);
  }


  protected buildResourceForm(){
    this.resourceForm = this.formBuilder.group({
      id: [null],
      name: [null, [Validators.required, Validators.minLength(2)]],
      description:  [null]
    })
  }

  protected creationPageTittle(): string {
    return "Cadastro de tipo de insumo"
  }

  protected editionPageTittle(): string {
    const categoryName = this.resource.name || "";
    return "Editando tipo de insumo: " + categoryName;
  }

}
