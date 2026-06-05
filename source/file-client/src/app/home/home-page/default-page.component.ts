import {Component, OnInit} from '@angular/core';
import {CommonTool} from '@ccxc/tool';
import {DiskManagementService} from '../disk-management/disk-management.service';
import {BUTTON_CODE} from '../../config/button-code';
import {ActivatedRoute, ParamMap} from '@angular/router';

@Component({
  selector: 'app-default-page',
  templateUrl: './default-page.component.html',
  styleUrls: ['./default-page.component.scss']
})
export class DefaultPageComponent implements OnInit {

  public userDiskInfo: { freeFlow, cloudSpace, useSpace };
  public BUTTON_CODE = BUTTON_CODE;
  public diskId;

  constructor(private diskManagementService: DiskManagementService,
              public activateRoute: ActivatedRoute) {
    this.userDiskInfo = {
      freeFlow: {value: '0', unit: 'kb'},
      cloudSpace: {value: '0', unit: 'kb'},
      useSpace: {value: '0', unit: 'kb'}
    };
  }

  ngOnInit() {
    this.activateRoute.queryParamMap.subscribe((params: ParamMap) => {
      this.diskId = params.get('diskId');
      if (this.diskId) {
        this.init();
      } else {
        this.userDiskInfo = {
          freeFlow: {value: '0', unit: 'kb'},
          cloudSpace: {value: '0', unit: 'kb'},
          useSpace: {value: '0', unit: 'kb'}
        };
      }
    });
  }

  init() {
    this.diskManagementService.getDisk(this.diskId).subscribe(res => {
      this.userDiskInfo = {
        freeFlow: CommonTool.getSpaceUnit(res.freeFlow),
        cloudSpace: CommonTool.getSpaceUnit(res.cloudSpace),
        useSpace: CommonTool.getSpaceUnit(res.useSpace)
      };
    });
  }
}
